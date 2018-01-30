package io.antiserver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import io.antiserver.api.Antiserver;
import io.antiserver.api.AntiserverJarDependency;
import io.antiserver.api.AntiserverMavenDependency;
import io.antiserver.api.AntiserverRequest;
import io.antiserver.api.AntiserverResponse;
import io.antiserver.container.ContainerLauncher;
import io.antiserver.container.DefaultContainerLauncher;
import io.antiserver.jar.DefaultJarManager;
import io.antiserver.jar.JarManager;
import io.antiserver.repository.DefaultRepositoryManager;
import io.antiserver.repository.RepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultAntiserver implements Antiserver {

    private static Logger LOG = LoggerFactory.getLogger(DefaultAntiserver.class);

    private RepositoryManager repositoryManager;

    private JarManager jarManager;

    private ContainerLauncher launcher;

    DefaultAntiserver(AntiserverConfig config) {
        this.repositoryManager = new DefaultRepositoryManager(config.getMavenRepositoryPath(), config.getBoms());
        this.jarManager = new DefaultJarManager(config.getJarTempPath());
        this.launcher = new DefaultContainerLauncher();
    }

    @Override
    public <T> CompletableFuture<AntiserverResponse<T>> process(AntiserverRequest<T> request) {
        long startTime = System.currentTimeMillis();
        CompletableFuture<List<URL>> mavenClasspath = repositoryManager.classpath(request.getDependencies().stream()
                .filter(AntiserverMavenDependency.class::isInstance)
                .map(AntiserverMavenDependency.class::cast)
                .collect(Collectors.toList()));

        CompletableFuture<List<URL>> jarClasspath = jarManager.classpath(request.getDependencies().stream()
                .filter(AntiserverJarDependency.class::isInstance)
                .map(AntiserverJarDependency.class::cast)
                .collect(Collectors.toList()));


        return mavenClasspath.thenCompose(maven -> jarClasspath.thenApply(jar -> {
            List<URL> combined = new ArrayList<>();
            combined.addAll(maven);
            combined.addAll(jar);
            return combined;
        })).thenCompose(launcher::launch)
                .thenApply(container -> container.getFunction(request.getFunction(), request.getOutputType()))
                .thenApply(function -> function.apply(request.getInput()))
                .thenApply(output -> {
                    LOG.info("Call to function " + request.getFunction() + " completed in " + (System.currentTimeMillis() - startTime) + " millis");
                    return new AntiserverResponse.Builder<T>()
                            .function(request.getFunction())
                            .output(output)
                            .build();
                });
    }

    @Override
    public CompletableFuture<Void> preload(List<AntiserverMavenDependency> dependencies) {
        return repositoryManager.preload(dependencies)
                .thenApply(urls -> null);
    }
}

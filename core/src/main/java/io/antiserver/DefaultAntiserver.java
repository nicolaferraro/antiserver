package io.antiserver;

import java.util.concurrent.CompletableFuture;

import io.antiserver.container.ContainerLauncher;
import io.antiserver.container.DefaultContainerLauncher;
import io.antiserver.model.AntiserverConfig;
import io.antiserver.model.AntiserverRequest;
import io.antiserver.model.AntiserverResponse;
import io.antiserver.repository.DefaultRepositoryManager;
import io.antiserver.repository.RepositoryManager;

class DefaultAntiserver implements Antiserver {

    private RepositoryManager repositoryManager;

    private ContainerLauncher launcher;

    public DefaultAntiserver(AntiserverConfig config) {
        this.repositoryManager = new DefaultRepositoryManager(config.getMavenRepositoryPath(), config.getBoms());
        this.launcher = new DefaultContainerLauncher();
    }

    @Override
    public CompletableFuture<AntiserverResponse> process(AntiserverRequest request) {
        return repositoryManager.classpath(request.getDependencies())
                .thenCompose(launcher::launch)
                .thenApply(container -> container.getFunction(request.getFunction(), request.getOutputType()))
                .thenApply(function -> function.apply(request.getInput()))
                .thenApply(output -> new AntiserverResponse(request.getFunction(), output));
    }

}

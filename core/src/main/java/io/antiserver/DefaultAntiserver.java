package io.antiserver;

import java.util.concurrent.CompletableFuture;

import io.antiserver.container.ContainerLauncher;
import io.antiserver.container.DefaultContainerLauncher;
import io.antiserver.api.Antiserver;
import io.antiserver.api.AntiserverRequest;
import io.antiserver.api.AntiserverResponse;
import io.antiserver.repository.DefaultRepositoryManager;
import io.antiserver.repository.RepositoryManager;

class DefaultAntiserver implements Antiserver {

    private RepositoryManager repositoryManager;

    private ContainerLauncher launcher;

    DefaultAntiserver(AntiserverConfig config) {
        this.repositoryManager = new DefaultRepositoryManager(config.getMavenRepositoryPath(), config.getBoms());
        this.launcher = new DefaultContainerLauncher();
    }

    @Override
    public <T> CompletableFuture<AntiserverResponse<T>> process(AntiserverRequest<T> request) {
        return repositoryManager.classpath(request.getDependencies())
                .thenCompose(launcher::launch)
                .thenApply(container -> container.getFunction(request.getFunction(), request.getOutputType()))
                .thenApply(function -> function.apply(request.getInput()))
                .thenApply(output -> new AntiserverResponse.Builder<T>()
                        .function(request.getFunction())
                        .output(output)
                        .build());
    }

}

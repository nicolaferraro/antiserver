package io.antiserver.repository;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RepositoryManager {

    CompletableFuture<List<URL>> classpath(List<RunDependency> dependencies);

}

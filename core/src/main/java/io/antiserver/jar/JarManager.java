package io.antiserver.jar;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.antiserver.api.AntiserverJarDependency;

public interface JarManager {

    CompletableFuture<List<URL>> classpath(List<AntiserverJarDependency> dependencies);

}

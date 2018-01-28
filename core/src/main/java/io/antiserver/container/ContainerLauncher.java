package io.antiserver.container;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ContainerLauncher {

    CompletableFuture<Container> launch(List<URL> classpath);

}

package io.antiserver.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import io.antiserver.api.AntiserverJarDependency;
import org.apache.commons.io.IOUtils;

public class DefaultJarManager implements JarManager {

    private File baseDir;

    public DefaultJarManager(String basePath) {
        this.baseDir = new File(basePath);
        if (!this.baseDir.mkdirs()) {
            throw new RuntimeException("Unable to create base dir for jar files in " + basePath);
        }
    }

    @Override
    public CompletableFuture<List<URL>> classpath(List<AntiserverJarDependency> dependencies) {
        return CompletableFuture.supplyAsync(() -> dependencies.stream()
                .map(this::save)
                .collect(Collectors.toList()));
    }

    private URL save(AntiserverJarDependency jar) {
        try {
            File file = File.createTempFile("antiserver-", ".jar", baseDir);
            file.deleteOnExit();

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                IOUtils.copy(jar.getFileStream(), fileOutputStream);
            }
            return file.toURI().toURL();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

package io.antiserver.api;

import java.io.InputStream;
import java.util.Objects;

public class AntiserverJarDependency implements AntiserverDependency {

    private InputStream fileStream;

    public AntiserverJarDependency(InputStream fileStream) {
        this.fileStream = Objects.requireNonNull(fileStream);
    }

    public InputStream getFileStream() {
        return fileStream;
    }

}

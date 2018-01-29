package io.antiserver.api;

import java.util.Objects;

public class AntiserverMavenDependency implements AntiserverDependency {

    private String gav;

    public AntiserverMavenDependency(String gav) {
        this.gav = Objects.requireNonNull(gav);
    }

    public String getGav() {
        return gav;
    }
}

package io.antiserver.repository;

import java.util.Objects;

public class RunMavenDependency implements RunDependency {

    private String gav;

    public RunMavenDependency(String gav) {
        this.gav = Objects.requireNonNull(gav);
    }

    public String getGav() {
        return gav;
    }
}

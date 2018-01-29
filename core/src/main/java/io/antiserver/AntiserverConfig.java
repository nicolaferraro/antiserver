package io.antiserver;

import java.util.ArrayList;
import java.util.List;

import io.antiserver.api.AntiserverMavenDependency;

public class AntiserverConfig {

    private String mavenRepositoryPath;

    private String jarTempPath;

    private List<AntiserverMavenDependency> boms = new ArrayList<>();

    public AntiserverConfig() {
    }

    public String getMavenRepositoryPath() {
        return mavenRepositoryPath;
    }

    public void setMavenRepositoryPath(String mavenRepositoryPath) {
        this.mavenRepositoryPath = mavenRepositoryPath;
    }

    public String getJarTempPath() {
        return jarTempPath;
    }

    public void setJarTempPath(String jarTempPath) {
        this.jarTempPath = jarTempPath;
    }

    public List<AntiserverMavenDependency> getBoms() {
        return boms;
    }

    public void setBoms(List<AntiserverMavenDependency> boms) {
        this.boms = boms;
    }
}

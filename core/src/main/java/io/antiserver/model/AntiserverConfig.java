package io.antiserver.model;

import java.util.ArrayList;
import java.util.List;

public class AntiserverConfig {

    public static final String DEFAULT_MAVEN_REPOSITORY_PATH = "target/repo";

    private String mavenRepositoryPath = DEFAULT_MAVEN_REPOSITORY_PATH;

    private List<AntiserverMavenDependency> boms = new ArrayList<>();

    public AntiserverConfig() {
    }

    public String getMavenRepositoryPath() {
        return mavenRepositoryPath;
    }

    public void setMavenRepositoryPath(String mavenRepositoryPath) {
        this.mavenRepositoryPath = mavenRepositoryPath;
    }

    public List<AntiserverMavenDependency> getBoms() {
        return boms;
    }

    public void setBoms(List<AntiserverMavenDependency> boms) {
        this.boms = boms;
    }
}

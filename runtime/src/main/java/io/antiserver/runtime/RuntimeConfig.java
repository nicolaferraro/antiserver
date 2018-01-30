package io.antiserver.runtime;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.antiserver.api.AntiserverMavenDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeConfig {

    private static Logger LOG = LoggerFactory.getLogger(RuntimeConfig.class);

    private static final String ENV_MAVEN_REPOSITORY_PATH = "ANTISERVER_MAVEN_REPOSITORY_PATH";
    private static final String ENV_MAVEN_BOMS = "ANTISERVER_MAVEN_BOMS";
    private static final String ENV_PRELOADED_ARTIFACTS = "ANTISERVER_PRELOADED_ARTIFACTS";

    public int getPort() {
        return 8080;
    }

    public List<AntiserverMavenDependency> getBoms() {
        String bomCsv = getEnv(ENV_MAVEN_BOMS, () -> "");
        return Arrays.stream(bomCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(AntiserverMavenDependency::new)
                .collect(Collectors.toList());
    }

    public String getJarTempPath() {
        String basePath = getEnv("JAVA_DATA_DIR", () -> "/tmp");
        String jarPath = new File(basePath, "antiserver/jars").getAbsolutePath();
        LOG.info("Using jar path: " + jarPath);
        return jarPath;
    }

    public String getMavenRepositoryPath() {
        String path = getEnv(ENV_MAVEN_REPOSITORY_PATH, () -> {
            String userHome = System.getProperty("user.home");
            if (userHome != null && userHome.length() > 1) {
                return new File(userHome, ".m2/repository").getAbsolutePath();
            } else if (getEnv("JAVA_DATA_DIR", () -> null) != null) {
                return new File(getEnv("JAVA_DATA_DIR", () -> null), "antiserver/repository").getAbsolutePath();
            }
            return "/tmp/antiserver-temp-repository";
        });
        LOG.info("Using repository location: " + path);
        return path;
    }

    public List<AntiserverMavenDependency> getPreloadedArtifacts() {
        String bomCsv = getEnv(ENV_PRELOADED_ARTIFACTS, () -> "");
        return Arrays.stream(bomCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(AntiserverMavenDependency::new)
                .collect(Collectors.toList());
    }

    private String getEnv(String key, Supplier<String> defaultValue) {
        String value = System.getenv(key);
        if (value == null) {
            return defaultValue.get();
        }
        return value;
    }


}

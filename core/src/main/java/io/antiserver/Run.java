package io.antiserver;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import io.antiserver.container.ContainerLauncher;
import io.antiserver.container.DefaultContainerLauncher;
import io.antiserver.repository.DefaultRepositoryManager;
import io.antiserver.repository.RunMavenDependency;
import io.antiserver.repository.RepositoryManager;

public class Run {

    public static void main(String[] args) throws Exception {
        RepositoryManager repositoryManager = new DefaultRepositoryManager(Arrays.asList(
                new RunMavenDependency("org.springframework.boot:spring-boot-dependencies:pom:1.5.9.RELEASE"),
                new RunMavenDependency("org.apache.camel:camel-spring-boot-dependencies:pom:2.20.2.RELEASE")
        ));

        ContainerLauncher launcher = new DefaultContainerLauncher();



        Map<String, String> obj = new TreeMap<>();
        obj.put("name", "Cippa");


        String result = repositoryManager.classpath(Arrays.asList(
                    new RunMavenDependency("org.apache.camel:camel-core:2.20.2"),
                    new RunMavenDependency("org.springframework.boot:spring-boot-starter-web:1.5.9.RELEASE"),
                    new RunMavenDependency("io.antiserver.examples:antiserver-examples-simple:1.0-SNAPSHOT")))
                .thenCompose(launcher::launch)
                .thenApply(c -> c.getFunction("example2", String.class))
                .thenApply(f -> f.apply(obj))
                .get();

        System.out.println(result);
    }

}

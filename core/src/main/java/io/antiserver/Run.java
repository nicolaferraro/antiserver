package io.antiserver;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import io.antiserver.model.AntiserverConfig;
import io.antiserver.model.AntiserverRequest;
import io.antiserver.model.AntiserverResponse;
import io.antiserver.model.AntiserverMavenDependency;

public class Run {

    public static void main(String[] args) throws Exception {

        AntiserverConfig config = new AntiserverConfig();
        config.setMavenRepositoryPath("/home/nferraro/.m2/repository");
        config.setBoms(Arrays.asList(
                new AntiserverMavenDependency("org.springframework.boot:spring-boot-dependencies:pom:1.5.9.RELEASE"),
                new AntiserverMavenDependency("org.apache.camel:camel-spring-boot-dependencies:pom:2.20.2.RELEASE")
        ));

        Antiserver antiserver = Antiserver.create(config);


        Map<String, String> obj = new TreeMap<>();
        obj.put("name", "Cippa");

        AntiserverRequest request = new AntiserverRequest("example2", obj, String.class, Arrays.asList(
                new AntiserverMavenDependency("org.apache.camel:camel-core:2.20.2"),
                new AntiserverMavenDependency("org.springframework.boot:spring-boot-starter-web:1.5.9.RELEASE"),
                new AntiserverMavenDependency("io.antiserver.examples:antiserver-examples-simple:1.0-SNAPSHOT"))
        );

        AntiserverResponse response = antiserver.process(request).get();
        System.out.println(response.getOutput());
    }

}

package io.antiserver;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import io.antiserver.api.Antiserver;
import io.antiserver.api.AntiserverRequest;
import io.antiserver.api.AntiserverResponse;
import io.antiserver.api.AntiserverMavenDependency;

public class Test {

    public static void main(String[] args) throws Exception {

        AntiserverConfig config = new AntiserverConfig();
        config.setMavenRepositoryPath("/home/nferraro/.m2/repository");
        config.setBoms(Arrays.asList(
                new AntiserverMavenDependency("org.springframework.boot:spring-boot-dependencies:pom:1.5.9.RELEASE"),
                new AntiserverMavenDependency("org.apache.camel:camel-spring-boot-dependencies:pom:2.20.2.RELEASE")
        ));

        Antiserver antiserver = AntiserverFactory.create(config);

        Map<String, String> obj = new TreeMap<>();
        obj.put("name", "Cippa");

        AntiserverRequest<String> request = new AntiserverRequest.Builder<String>()
                .function("example2")
                .input(obj)
                .outputType(String.class)
                .addMavenDependency("org.apache.camel:camel-core:2.20.2")
                .addMavenDependency("org.springframework.boot:spring-boot-starter-web:1.5.9.RELEASE")
                .addMavenDependency("io.antiserver.examples:antiserver-examples-simple:1.0-SNAPSHOT")
                .build();

        AntiserverResponse<String> response = antiserver.process(request).get();
        System.out.println(response.getOutput());
    }

}

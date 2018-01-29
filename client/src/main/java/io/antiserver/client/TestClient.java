package io.antiserver.client;

import java.io.FileInputStream;
import java.util.Map;
import java.util.TreeMap;

import io.antiserver.api.AntiserverRequest;

public class TestClient {

    public static void main(String[] args) throws Exception {

        AntiserverClient client = AntiserverClientFactory.create("localhost", 8080);

        Map<String, String> data = new TreeMap<>();
        data.put("name", "World!");


        System.out.println(client.process(new AntiserverRequest.Builder<String>()
                .outputType(String.class)
                .input(data)
                .function("camel")
                .addMavenDependency("org.springframework.boot:spring-boot-starter-web:1.5.9.RELEASE")
                .addMavenDependency("io.antiserver.examples:antiserver-examples-simple:1.0-SNAPSHOT")
                .addJarDependency(new FileInputStream("examples/simple/target/antiserver-examples-simple-1.0-SNAPSHOT.jar"))
                .build()).get().getOutput());

        System.out.println(client.process(new AntiserverRequest.Builder<String>()
                .outputType(String.class)
                .input(data)
                .function("camel")
                .addMavenDependency("org.apache.camel:camel-core:2.20.2")
                .addJarDependency(new FileInputStream("examples/simple/target/antiserver-examples-simple-1.0-SNAPSHOT.jar"))
                .build()).get().getOutput());
    }

}

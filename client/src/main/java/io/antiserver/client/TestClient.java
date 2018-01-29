package io.antiserver.client;

import java.util.Map;
import java.util.TreeMap;

import io.antiserver.api.AntiserverRequest;
import io.antiserver.api.AntiserverResponse;

public class TestClient {

    public static void main(String[] args) throws Exception {

        AntiserverClient client = AntiserverClientFactory.create("localhost", 8080);

        Map<String, String> data = new TreeMap<>();
        data.put("name", "World!");


        AntiserverRequest<String> request = new AntiserverRequest.Builder<String>()
                .outputType(String.class)
                .input(data)
                .function("camel")
                .addMavenDependency("org.springframework.boot:spring-boot-starter-web:1.5.9.RELEASE")
                .addMavenDependency("io.antiserver.examples:antiserver-examples-simple:1.0-SNAPSHOT")
                .build();

        AntiserverResponse<String> response = client.process(request).get();
        System.out.println(response.getOutput());

    }

}

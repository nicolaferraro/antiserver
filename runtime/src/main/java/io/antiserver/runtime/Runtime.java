package io.antiserver.runtime;

import java.util.Arrays;

import io.antiserver.AntiserverConfig;
import io.antiserver.AntiserverFactory;
import io.antiserver.api.Antiserver;
import io.antiserver.api.AntiserverMavenDependency;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class Runtime {

    public static void main(String[] args) throws Exception {

        AntiserverConfig config = new AntiserverConfig();
        config.setMavenRepositoryPath("/home/nferraro/.m2/repository");
        config.setBoms(Arrays.asList(
                new AntiserverMavenDependency("org.springframework.boot:spring-boot-dependencies:pom:1.5.9.RELEASE"),
                new AntiserverMavenDependency("org.apache.camel:camel-spring-boot-dependencies:pom:2.20.2.RELEASE")
        ));

        Antiserver antiserver = AntiserverFactory.create(config);

        RemoteAntiserverService antiserverService = new RemoteAntiserverService(antiserver);

        Server server = ServerBuilder.forPort(8080)
                .addService(antiserverService)
                .build()
                .start();

        java.lang.Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.awaitTermination();
    }

}

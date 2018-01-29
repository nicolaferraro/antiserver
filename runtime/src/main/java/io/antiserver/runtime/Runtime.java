package io.antiserver.runtime;

import io.antiserver.AntiserverConfig;
import io.antiserver.AntiserverFactory;
import io.antiserver.api.Antiserver;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runtime {

    private static Logger LOG = LoggerFactory.getLogger(Runtime.class);

    public static void main(String[] args) throws Exception {

        RuntimeConfig runtime = new RuntimeConfig();

        AntiserverConfig config = new AntiserverConfig();
        config.setMavenRepositoryPath(runtime.getMavenRepositoryPath());
        config.setJarTempPath(runtime.getJarTempPath());
        config.setBoms(runtime.getBoms());

        Antiserver antiserver = AntiserverFactory.create(config);

        RemoteAntiserverService antiserverService = new RemoteAntiserverService(antiserver);

        Server server = ServerBuilder.forPort(runtime.getPort())
                .addService(antiserverService)
                .build()
                .start();

        java.lang.Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        LOG.info("Antiserver started on port " + runtime.getPort());
        server.awaitTermination();
    }

}

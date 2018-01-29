package io.antiserver.client;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.protobuf.ByteString;
import io.antiserver.api.AntiserverJarDependency;
import io.antiserver.api.AntiserverMavenDependency;
import io.antiserver.api.AntiserverRequest;
import io.antiserver.api.AntiserverResponse;
import io.antiserver.api.Serializer;
import io.antiserver.protocol.RemoteAntiserverRequest;
import io.antiserver.protocol.RemoteAntiserverResponse;
import io.antiserver.protocol.RemoteAntiserverServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.io.IOUtils;

class DefaultAntiserverClient implements AntiserverClient {

    private RemoteAntiserverServiceGrpc.RemoteAntiserverServiceBlockingStub stub;

    public DefaultAntiserverClient(String host, int port) {
        Channel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
        this.stub = RemoteAntiserverServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public <T> CompletableFuture<AntiserverResponse<T>> process(AntiserverRequest<T> antiserverRequest) {
        // TODO make really async
        return CompletableFuture.supplyAsync(() -> {
            Serializer serializer = new Serializer();

            RemoteAntiserverRequest remoteRequest = RemoteAntiserverRequest.newBuilder()
                    .setFunction(antiserverRequest.getFunction())
                    .setInput(ByteString.copyFrom(serializer.serialize(antiserverRequest.getInput())))
                    .addAllMavenDependencies(antiserverRequest.getDependencies().stream()
                            .filter(AntiserverMavenDependency.class::isInstance)
                            .map(AntiserverMavenDependency.class::cast)
                            .map(AntiserverMavenDependency::getGav)
                            .collect(Collectors.toList())
                    )
                    .addAllJarDependencies(antiserverRequest.getDependencies().stream()
                            .filter(AntiserverJarDependency.class::isInstance)
                            .map(AntiserverJarDependency.class::cast)
                            .map(this::toByteString)
                            .collect(Collectors.toList())
                    )
                    .build();

            RemoteAntiserverResponse remoteResponse = stub.invoke(remoteRequest);

            return new AntiserverResponse.Builder<T>()
                    .function(remoteResponse.getFunction())
                    .output(serializer.deserialize(remoteResponse.getOutput().toByteArray(), antiserverRequest.getOutputType()))
                    .build();
        });
    }

    private ByteString toByteString(AntiserverJarDependency dependency) {
        try {
            return ByteString.copyFrom(IOUtils.toByteArray(dependency.getFileStream()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}

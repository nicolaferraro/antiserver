package io.antiserver.runtime;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.ByteString;
import io.antiserver.api.Antiserver;
import io.antiserver.api.AntiserverJarDependency;
import io.antiserver.api.AntiserverMavenDependency;
import io.antiserver.api.AntiserverRequest;
import io.antiserver.api.Serializer;
import io.antiserver.protocol.RemoteAntiserverPreloadRequest;
import io.antiserver.protocol.RemoteAntiserverPreloadResponse;
import io.antiserver.protocol.RemoteAntiserverRequest;
import io.antiserver.protocol.RemoteAntiserverResponse;
import io.antiserver.protocol.RemoteAntiserverServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RemoteAntiserverService extends RemoteAntiserverServiceGrpc.RemoteAntiserverServiceImplBase {

    private static Logger LOG = LoggerFactory.getLogger(RemoteAntiserverService.class);

    private Antiserver antiserver;

    public RemoteAntiserverService(Antiserver antiserver) {
        this.antiserver = antiserver;
    }

    @Override
    public void invoke(RemoteAntiserverRequest remoteRequest, StreamObserver<RemoteAntiserverResponse> responseObserver) {

        Serializer serializer = new Serializer();

        AntiserverRequest<JsonNode> request = new AntiserverRequest.Builder<JsonNode>()
                .outputType(JsonNode.class)
                .function(remoteRequest.getFunction())
                .input(serializer.deserialize(remoteRequest.getInput().newInput(), Object.class))
                .dependencies(
                        Stream.concat(
                                remoteRequest.getMavenDependenciesList().stream()
                                    .map(AntiserverMavenDependency::new),
                                remoteRequest.getJarDependenciesList().stream()
                                        .map(ByteString::newInput)
                                        .map(AntiserverJarDependency::new)
                        ).collect(Collectors.toList())
                )
                .build();

        antiserver.process(request)
                .thenApply(response -> {
                    RemoteAntiserverResponse remoteResponse = RemoteAntiserverResponse.newBuilder()
                            .setFunction(response.getFunction())
                            .setOutput(ByteString.copyFrom(serializer.serialize(response.getOutput())))
                            .build();

                    responseObserver.onNext(remoteResponse);
                    responseObserver.onCompleted();
                    return null;
                }).exceptionally(ex -> {
                    LOG.error("Error while processing request", ex);
                    responseObserver.onError(ex);
                    return null;
                });
    }

    @Override
    public void preload(RemoteAntiserverPreloadRequest request, StreamObserver<RemoteAntiserverPreloadResponse> responseObserver) {
        List<AntiserverMavenDependency> dependencies = request.getMavenDependenciesList().stream()
                .map(AntiserverMavenDependency::new)
                .collect(Collectors.toList());

        antiserver.preload(dependencies)
                .thenApply(response -> {
                    responseObserver.onNext(RemoteAntiserverPreloadResponse.newBuilder().build());
                    responseObserver.onCompleted();
                    return null;
                }).exceptionally(ex -> {
                    LOG.error("Error while preloading dependencies", ex);
                    responseObserver.onError(ex);
                    return null;
                });
    }
}

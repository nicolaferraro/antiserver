package io.antiserver;

import java.util.concurrent.CompletableFuture;

import io.antiserver.model.AntiserverConfig;
import io.antiserver.model.AntiserverRequest;
import io.antiserver.model.AntiserverResponse;

public interface Antiserver {

    static Antiserver create(AntiserverConfig config) {
        return new DefaultAntiserver(config);
    }

    CompletableFuture<AntiserverResponse> process(AntiserverRequest request);

}

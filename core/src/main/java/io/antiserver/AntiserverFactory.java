package io.antiserver;

import io.antiserver.api.Antiserver;

public interface AntiserverFactory {

    static Antiserver create(AntiserverConfig config) {
        return new DefaultAntiserver(config);
    }

}

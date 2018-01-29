package io.antiserver.client;

public interface AntiserverClientFactory {

    static AntiserverClient create(String host, int port) {
        return new DefaultAntiserverClient(host, port);
    }

}

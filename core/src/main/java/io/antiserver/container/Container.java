package io.antiserver.container;

import java.io.Closeable;
import java.util.List;
import java.util.function.Function;

public interface Container extends Closeable {

    List<String> getFunctionNames();

    <T, R> Function<T, R> getFunction(String function, Class<R> outputType);

}

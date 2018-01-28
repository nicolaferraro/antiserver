package io.antiserver.container;

import java.util.List;
import java.util.function.Function;

public interface Container {

    List<String> getFunctionNames();

    <T, R> Function<T, R> getFunction(String function, Class<R> outputType);

}

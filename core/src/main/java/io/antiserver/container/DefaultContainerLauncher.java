package io.antiserver.container;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.antiserver.Serializer;

public class DefaultContainerLauncher implements ContainerLauncher {

    private static final String FUNCTIONS_FILE = "META-INF/antiserver/functions";

    @Override
    public CompletableFuture<Container> launch(List<URL> classpath) {
        try {
            ClassLoader classLoader = new URLClassLoader(classpath.toArray(new URL[]{}));

            Properties properties = new Properties();
            try (InputStream resource = classLoader.getResourceAsStream(FUNCTIONS_FILE)) {
                if (resource == null) {
                    throw new IllegalArgumentException("Cannot find manifest in " + FUNCTIONS_FILE);
                }
                properties.load(resource);
            }

            return CompletableFuture.supplyAsync(() -> new Container() {
                @Override
                public List<String> getFunctionNames() {
                    return new ArrayList<>(properties.stringPropertyNames());
                }

                @Override
                public <T, R> Function<T, R> getFunction(String function, Class<R> outputType) {
                    try {
                        String classMethod = properties.getProperty(function);
                        int split = classMethod.indexOf("#");
                        if (split <= 0 || split >= classMethod.length() - 1 || split != classMethod.lastIndexOf("#")) {
                            throw new IllegalStateException("Illegal definition for function " + function);
                        }

                        String className = classMethod.substring(0, split);
                        String methodName = classMethod.substring(split + 1);

                        Class<?> clazz = classLoader.loadClass(className);
                        Object instance = clazz.newInstance();
                        List<Method> methods = Arrays.stream(clazz.getMethods())
                                .filter(m -> m.getName().equals(methodName))
                                .collect(Collectors.toList());

                        if (methods.size() == 0) {
                            throw new IllegalStateException("Cannot find method named " + methodName);
                        } else if (methods.size() > 1) {
                            throw new IllegalStateException("Multiple method named " + methodName + " found");
                        }
                        Method method = methods.get(0);

                        if (method.getParameterCount() != 1) {
                            throw new IllegalStateException("Method " + methodName + " accepts multiple parameters. Only one is supported");
                        }

                        return data -> {
                            try {
                                Serializer localSerializer = new Serializer();

                                byte[] serializedData = localSerializer.serialize(data);
                                Object deserializedData = localSerializer.deserialize(serializedData, method.getParameterTypes()[0]);

                                Object resultObj = method.invoke(instance, deserializedData);

                                byte[] result = localSerializer.serialize(resultObj);
                                return localSerializer.deserialize(result, outputType);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        };

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

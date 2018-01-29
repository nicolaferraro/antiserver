package io.antiserver.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AntiserverRequest<T> {

    private String function;

    private Object input;

    private Class<T> outputType;

    private List<AntiserverDependency> dependencies;

    private AntiserverRequest() {
    }

    public String getFunction() {
        return function;
    }

    public Object getInput() {
        return input;
    }

    public List<AntiserverDependency> getDependencies() {
        return dependencies;
    }

    public Class<T> getOutputType() {
        return outputType;
    }

    public static class Builder<T> {

        private AntiserverRequest<T> request;

        @SuppressWarnings("unchecked")
        public Builder() {
            this.request = new AntiserverRequest<>();
            this.request.dependencies = new ArrayList<>();
        }

        public Builder<T> function(String function) {
            this.request.function = function;
            return this;
        }

        public Builder<T> input(Object input) {
            this.request.input = input;
            return this;
        }

        public Builder<T> outputType(Class<T> outputType) {
            this.request.outputType = outputType;
            return this;
        }

        public Builder<T> dependencies(List<AntiserverDependency> dependencies) {
            this.request.dependencies.clear();
            this.request.dependencies.addAll(dependencies);
            return this;
        }

        public Builder<T> addDependency(AntiserverDependency dependency) {
            this.request.dependencies.add(dependency);
            return this;
        }

        public Builder<T> addMavenDependency(String gav) {
            return this.addDependency(new AntiserverMavenDependency(gav));
        }

        public Builder<T> addJarDependency(InputStream fileStream) {
            return this.addDependency(new AntiserverJarDependency(fileStream));
        }

        public AntiserverRequest<T> build() {
            Objects.requireNonNull(request.function);
            Objects.requireNonNull(request.input);
            Objects.requireNonNull(request.outputType);
            Objects.requireNonNull(request.dependencies);
            return request;
        }
    }

}

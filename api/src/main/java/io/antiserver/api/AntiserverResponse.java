package io.antiserver.api;

import java.util.Objects;

public class AntiserverResponse<T> {

    private String function;

    private T output;

    private AntiserverResponse() {
    }

    public String getFunction() {
        return function;
    }

    public T getOutput() {
        return output;
    }

    public static class Builder<T> {

        private AntiserverResponse<T> response;

        public Builder() {
            this.response = new AntiserverResponse<>();
        }

        public Builder<T> function(String function) {
            this.response.function = function;
            return this;
        }

        public Builder<T> output(T output) {
            this.response.output = output;
            return this;
        }

        public AntiserverResponse<T> build() {
            Objects.requireNonNull(response.function);
            Objects.requireNonNull(response.output);
            return response;
        }
    }

}

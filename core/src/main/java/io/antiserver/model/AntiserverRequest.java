package io.antiserver.model;

import java.util.List;

public class AntiserverRequest {

    private String function;

    private Object input;

    private Class<?> outputType;

    private List<AntiserverDependency> dependencies;

    public AntiserverRequest() {
    }

    public AntiserverRequest(String function, Object input, Class<?> outputType, List<AntiserverDependency> dependencies) {
        this.function = function;
        this.input = input;
        this.outputType = outputType;
        this.dependencies = dependencies;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public List<AntiserverDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<AntiserverDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public Class<?> getOutputType() {
        return outputType;
    }

    public void setOutputType(Class<?> outputType) {
        this.outputType = outputType;
    }

}

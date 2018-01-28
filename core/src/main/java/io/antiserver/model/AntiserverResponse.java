package io.antiserver.model;

public class AntiserverResponse {

    private String function;

    private Object output;

    public AntiserverResponse() {
    }

    public AntiserverResponse(String function, Object output) {
        this.function = function;
        this.output = output;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        this.output = output;
    }

}

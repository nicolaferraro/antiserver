package my.example;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class Function {

    public Object funk(Object input) {
        return "Hello " + input;
    }

    public Object funk2(Person input) {
        return "Hello " + input.getName();
    }

    public String camel(Person input) {
        CamelContext context = new DefaultCamelContext();
        return context.getName();
    }

}

package demo.managers;

import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericManager {

    @Autowired
    private Tracer tracer;

    public void embeddedSpan(String name) throws InterruptedException {
        // A span within the span.
        try (var ignored = tracer.buildSpan("embedded span").withTag("name", name).startActive(true)) {
            randomFunction("function one");
            randomFunction("function two");
            randomFunction("function three");
        }
    }

    private void randomFunction(String name) throws InterruptedException {
        try(var ignored = tracer.buildSpan(name).startActive(true)) {
            Thread.sleep((long) (Math.random() * 1000));
        }
    }
}

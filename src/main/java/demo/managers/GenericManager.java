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
        var span = tracer.buildSpan("embedded span").withTag("name", name).start();
        try (var ignored = tracer.activateSpan(span)) {
            randomFunction("function one");
            randomFunction("function two");
            randomFunction("function three");
        } finally {
            span.finish();
        }
    }

    private void randomFunction(String name) throws InterruptedException {
        var span = tracer.buildSpan(name).start();
        try (var ignored = tracer.activateSpan(span)) {
            Thread.sleep((long) (Math.random() * 1000));
        } finally {
            span.finish();
        }
    }
}

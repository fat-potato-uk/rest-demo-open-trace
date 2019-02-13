package demo.controllers;


import demo.managers.GenericManager;
import demo.models.Greeting;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    GenericManager manager;

    @Autowired
    Tracer tracer;

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) throws InterruptedException {
        manager.embeddedSpan(name);
        // Propagate all baggage.
        tracer.activeSpan().context().baggageItems().forEach(i -> tracer.activeSpan().setBaggageItem(i.getKey(), i.getValue()));
        // Can tag the current span
        tracer.activeSpan().setTag("previous-count", counter.get());
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @RequestMapping("/errorgreeting")
    public Greeting errorGreeeting(@RequestParam(value="name", defaultValue="World") String name,
                                   @RequestParam(value="throw", required = false, defaultValue="true") Boolean doThrow) throws Exception {
        if(doThrow) {
            throw new Exception("bad times");
        }
        return greeting(name);

    }

    @RequestMapping("/generalerror")
    public Greeting manual(@RequestParam(value="name", defaultValue="World") String name) throws Exception {
        error();
        return greeting(name);
    }

    private void error() {
        Scope scope = tracer.scopeManager().active();
        if (scope != null) {
            Span span = scope.span();
            Tags.ERROR.set(span, true);
            span.log(Map.of(Fields.EVENT, "error", Fields.MESSAGE, "Totally borked mate"));
        }
    }
}
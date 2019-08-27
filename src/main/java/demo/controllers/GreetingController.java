package demo.controllers;


import demo.managers.GenericManager;
import demo.models.Greeting;
import demo.repositories.GreetingRepository;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private GenericManager manager;

    @Autowired
    private Tracer tracer;

    @Autowired
    private GreetingRepository greetingRepository;

    @GetMapping("/greeting")
    Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) throws InterruptedException {
        getBaggageAndSetName("/greeting");

        // Can tag the current span
        tracer.activeSpan().setTag("previous-count", counter.get());
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/greeting/embedded")
    Greeting embeddedGreeting(@RequestParam(value="name", defaultValue="World") String name) throws InterruptedException {
        getBaggageAndSetName("/greeting/embedded");

        // Run a function that creates span within this span
        manager.embeddedSpan(name);

        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/greeting/save")
    Greeting saveGreeting(@RequestParam(value="name", defaultValue="World") String name) {
        getBaggageAndSetName("/greeting/save");
        return greetingRepository.save(new Greeting(counter.incrementAndGet(), String.format(template, name)));
    }

    @GetMapping("/greeting/all")
    List<Greeting> allGreetings() {
        getBaggageAndSetName("/greeting/all");
        return greetingRepository.findAll();
    }

    @GetMapping("/greeting/throw")
    Greeting throwGreeting() {
        getBaggageAndSetName("/greeting/throw");
        throw new RuntimeException("Something went catastrophically wrong");
    }

    @GetMapping("/greeting/error")
    Greeting errorGreeting(@RequestParam(value="name", defaultValue="World") String name) throws Exception {
        getBaggageAndSetName("/greeting/error");
        Tags.ERROR.set(tracer.activeSpan(), true);
        tracer.activeSpan().log(Map.of(Fields.EVENT, "error", Fields.MESSAGE, "Something went a bit, but not catastrophically wrong"));
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    private void getBaggageAndSetName(String name) {
        tracer.activeSpan().setOperationName(name);

        // Propagate all baggage.
        var baggageItems = tracer.activeSpan().context().baggageItems();
        log.info("Our baggage: {}", baggageItems);
        baggageItems.forEach(i -> tracer.activeSpan().setBaggageItem(i.getKey(), i.getValue()));
    }
}
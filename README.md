# Open Trace Example

Open Trace is a open framework for monitoring activity within you application and micro-service
architecture. In their own works (taken from the https://opentracing.io website): 

_The OpenTracing API provides a standard, vendor neutral framework for instrumentation. 
This means that if a developer wants to try out a different distributed tracing system, 
then instead of repeating the whole instrumentation process for the new distributed tracing 
system, the developer can simply change the configuration of the Tracer._

To run this example, run the following command to start the Jaeger tracing all-in-one
docker container:

```bash
docker run -it --name jaeger 
-e COLLECTOR_ZIPKIN_HTTP_PORT=9411 
-p 5775:5775/udp
-p 6831:6831/udp
-p 6832:6832/udp
-p 5778:5778
-p 16686:16686
-p 14268:14268
-p 9411:9411
jaegertracing/all-in-one
```

This provides an endpoint for both sending our traces and viewing them. Next, run this
project (`mvn spring-boot:run`). Once it has started, try the following URLs:

* http://localhost:8080/greeting
* http://localhost:8080/greeting/embedded
* http://localhost:8080/greeting/save
* http://localhost:8080/greeting/all
* http://localhost:8080/greeting/throw
* http://localhost:8080/greeting/error

Now you can navigate to http://localhost:16686 to view the traces:

![Traces](Jaeger.png?raw=true "Traces")

To include baggage, run `curl -H "jaeger-baggage: key1=plastic, key2=paper" http://localhost:8080/greeting`

The `application.properties` includes a few interesting points, namely the sampler configuration: 

```
opentracing.jaeger.udp-sender.host=localhost
opentracing.jaeger.udp-sender.port=6831
opentracing.jaeger.probabilistic-sampler.sampling-rate = 1.0
spring.application.name=rest-trace-demo
```

This allows you to control the rate of sampling in your application. Other samplers can
be used based on [sampling](https://www.jaegertracing.io/docs/1.13/sampling) configuration.

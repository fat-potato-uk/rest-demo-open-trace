# rest-demo-open_trace_1
To Run:
* Run the following command:
```
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
* Start this project (`mvn spring-boot:run`)
* Open/curl either `http://localhost:8080/generalerror` or `http://localhost:8080/greeting`
* Navigate to `http://localhost:16686` to view the traces.
* To include baggage, run `curl -H "jaeger-baggage: key1=plastic, key2=paper" http://localhost:8080/greeting`

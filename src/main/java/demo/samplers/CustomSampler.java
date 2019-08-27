package demo.samplers;

import io.jaegertracing.internal.Constants;
import io.jaegertracing.internal.samplers.SamplingStatus;
import io.jaegertracing.spi.Sampler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CustomSampler implements Sampler {

    private static final Map<String, Object> TAGS = Map.of(Constants.SAMPLER_TYPE_TAG_KEY, CustomSampler.class.getSimpleName());

    @Override
    public SamplingStatus sample(String operation, long id) {
        // Only filer out database interactions

        log.error("HERE!!!!!!!! {}", operation);
        return SamplingStatus.of(true, TAGS);
    }

    /**
     * Only implemented to satisfy interface
     */
    @Override
    public void close() {
        // Do nothing
    }
}

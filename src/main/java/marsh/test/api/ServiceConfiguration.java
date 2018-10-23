package marsh.test.api;

import marsh.test.api.constants.Constants;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceConfiguration {


    @Bean("weather")
    public RestTemplate getWeatherService() {

        RestTemplate weatherService = new RestTemplateBuilder().rootUri(Constants.WEATHER_SERVICE_URL).build();
        return weatherService;

    }

    @Bean("geocoding")
    public RestTemplate getGeoCodingService() {

        RestTemplate geoCodingService = new RestTemplateBuilder().rootUri(Constants.GEOCODING_SERVICE_URL).build();
        return geoCodingService;
    }
}

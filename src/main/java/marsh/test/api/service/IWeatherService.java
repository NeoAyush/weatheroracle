package marsh.test.api.service;

import marsh.test.api.domain.WeatherResponse;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

public interface IWeatherService {

    WeatherResponse predictWeather(final String zipCode) throws Exception;
}

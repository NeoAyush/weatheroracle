package marsh.test.api.controller;

import marsh.test.api.domain.WeatherResponse;
import marsh.test.api.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @RequestMapping(value = "/city/{zip_code}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<WeatherResponse> getForecast(@PathVariable("zip_code") String zipCode) throws IOException {

        return new ResponseEntity<>(weatherService.predictWeather(zipCode), HttpStatus.OK);

    }

}


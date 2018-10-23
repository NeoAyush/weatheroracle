package marsh.test.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import marsh.test.api.constants.Constants;
import marsh.test.api.domain.Forecast;
import marsh.test.api.domain.WeatherResponse;
import marsh.test.api.exceptions.InternalAPICallFailedException;
import marsh.test.api.exceptions.InvalidZipCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class WeatherService implements IWeatherService {


    @Autowired
    @Qualifier("weather")
    private RestTemplate weatherService;

    @Autowired
    @Qualifier("geocoding")
    private RestTemplate geoCodingService;

    public WeatherResponse predictWeather(final String zipCode) throws RestClientException, IOException {

        WeatherResponse weatherResponse = new WeatherResponse();
        String[] latLongAddress;
        ResponseEntity<String> response = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
        List<Forecast> forecastList = new LinkedList<>();
        String timezone = null;

        try {
            latLongAddress = getLatLongFromZipCode(zipCode);
            String tomorrowDate = LocalDateTime.now().plusHours(Constants.PREDICT_FOR_TOMORROW).format(formatter);
            log.info("Calling Weather API to fetch Hourly Forecast for {} on date {}", zipCode, tomorrowDate);
            response = weatherService.getForEntity("/" + Constants.WEATHER_SERVICE_APP_KEY + "/" + latLongAddress[0] + "," + latLongAddress[1] + "," + tomorrowDate + "?exclude=minutely,daily", String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode n = mapper.readTree(response.getBody());
            timezone = n.get("timezone").textValue();

            for (int i = 0; i < Constants.PREDICT_FOR_TOMORROW; i++) {
                Forecast forecast = new Forecast();
                forecast.setHour(n.get("hourly").get("data").get(i).get("time").toString(), timezone);
                forecast.setTemperature(n.get("hourly").get("data").get(i).get("temperature").toString());
                forecastList.add(forecast);
            }

            weatherResponse.setCoolestTemperature(forecastList.stream().min((f1, f2) -> Double.valueOf(f1.getTemperature()).compareTo(Double.valueOf(f2.getTemperature()))).get().getTemperature());
            weatherResponse.setDate(tomorrowDate);
            weatherResponse.setZipCode(zipCode);
            weatherResponse.setCity(latLongAddress[2]);
            weatherResponse.setForecastList(forecastList);

        } catch (RestClientException e) {
            log.debug("Error fetching Weather Forecast data from API");
            e.printStackTrace();
            throw e;
        }
        log.info("Successfully fetched Hourly Forecast and displayed to user");
        return weatherResponse;

    }


    private String[] getLatLongFromZipCode(final String zipCode) throws InvalidZipCodeException, IOException, InternalAPICallFailedException {

        log.info("Calling Google Geo Coding API to fetch Latitude & Longitude for {}", zipCode);

        ResponseEntity<String> response = geoCodingService.getForEntity("/json?address=" + zipCode + "&key=" + Constants.GEOCODING_SERVICE_APP_KEY, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode n = mapper.readTree(response.getBody());
        String[] str = new String[3];
        if (n.get("status").textValue().equals("ZERO_RESULTS")) {
            log.debug("The API call failed with ZERO RESULTS error");
            throw new InvalidZipCodeException("Enter Valid Zip Code for regions of " + Constants.TARGET_COUNTRY + " only");
        }
        else if(!n.get("status").textValue().equals("OK")){

            log.debug("Geo Coding API call failed with error response {}" + response.getBody());
            throw new InternalAPICallFailedException("Internal API Failed");

        }
        int size = n.get("results").get(0).get("address_components").size();
        if (Constants.TARGET_COUNTRY.equalsIgnoreCase(n.get("results").get(0).get("address_components").get(size - 1).get("short_name").textValue())) {

            str[0] = n.get("results").get(0).get("geometry").get("location").get("lat").toString();
            str[1] = n.get("results").get(0).get("geometry").get("location").get("lng").toString();
            str[2] = n.get("results").get(0).get("formatted_address").textValue();
        } else {
            log.debug("Zip Code entered does not belong to the country {}", Constants.TARGET_COUNTRY);
            throw new InvalidZipCodeException("Enter Valid Zip Code for regions of " + Constants.TARGET_COUNTRY+ " only");
        }
        log.info("Successfully fetched Latitude {} & Longitude {} for city {}", str[0], str[1], str[2]);
        return str;

    }

}

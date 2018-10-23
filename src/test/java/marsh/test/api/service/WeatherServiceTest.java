package marsh.test.api.service;

import marsh.test.api.ServiceConfiguration;
import marsh.test.api.exceptions.InternalAPICallFailedException;
import marsh.test.api.exceptions.InvalidZipCodeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {ServiceConfiguration.class})
public class WeatherServiceTest {

    @Autowired
    @Qualifier("weather")
    @Mock
    private RestTemplate weatherService;

    @Autowired
    @Qualifier("geocoding")
    @Mock
    private RestTemplate geoCodingService;

    @InjectMocks
    private WeatherService ws;

    @Test(expected=InvalidZipCodeException.class)
    public void should_fail_for_invalid_zip_code_format() throws InvalidZipCodeException, IOException, InternalAPICallFailedException {

        when(geoCodingService.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(new ResponseEntity<>("{\"results\" : [],\"status\" : \"ZERO_RESULTS\"}", HttpStatus.OK));

        ws.predictWeather("abcdef");

    }

    @Test(expected=RestClientException.class)
    public void should_fail_when_weather_api_fails() throws IOException{

        when(geoCodingService.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(new ResponseEntity<>("{ \"results\" : [ { \"address_components\" : [ { \"long_name\" : \"90001\", \"short_name\" : \"90001\", \"types\" : [ \"postal_code\" ] }, { \"long_name\" : \"Los Angeles\", \"short_name\" : \"Los Angeles\", \"types\" : [ \"locality\", \"political\" ] }, { \"long_name\" : \"Los Angeles County\", \"short_name\" : \"Los Angeles County\", \"types\" : [ \"administrative_area_level_2\", \"political\" ] }, { \"long_name\" : \"California\", \"short_name\" : \"CA\", \"types\" : [ \"administrative_area_level_1\", \"political\" ] }, { \"long_name\" : \"United States\", \"short_name\" : \"US\", \"types\" : [ \"country\", \"political\" ] } ], \"formatted_address\" : \"Los Angeles, CA 90001, USA\", \"geometry\" : { \"bounds\" : { \"northeast\" : { \"lat\" : 33.9894491, \"lng\" : -118.231599 }, \"southwest\" : { \"lat\" : 33.948139, \"lng\" : -118.265182 } }, \"location\" : { \"lat\" : 33.9697897, \"lng\" : -118.2468148 }, \"location_type\" : \"APPROXIMATE\", \"viewport\" : { \"northeast\" : { \"lat\" : 33.9894491, \"lng\" : -118.231599 }, \"southwest\" : { \"lat\" : 33.948139, \"lng\" : -118.265182 } } }, \"place_id\" : \"ChIJHfsmIgbJwoARADaMiO5XZPM\", \"postcode_localities\" : [ \"Firestone Park\", \"Los Angeles\" ], \"types\" : [ \"postal_code\" ] } ], \"status\" : \"OK\" } ", HttpStatus.OK));
        doThrow(new RestClientException("Server Error")).when(weatherService).getForEntity(Mockito.anyString(), Mockito.any());
        ws.predictWeather("90001");
    }

}

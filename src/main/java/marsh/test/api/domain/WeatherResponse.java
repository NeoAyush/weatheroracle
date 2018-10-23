package marsh.test.api.domain;

import lombok.Data;

import java.util.List;

@Data
public class WeatherResponse {

    private String date;
    private String city;
    private String zipCode;
    private String coolestTemperature;
    private List<Forecast> forecastList;

}

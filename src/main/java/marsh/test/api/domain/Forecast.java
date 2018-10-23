package marsh.test.api.domain;

import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class Forecast {

    private String hour;
    private String temperature;

    public void setHour(final String time, final String timeZone) {

        this.hour = ZonedDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(time)), ZoneId.of(timeZone)).toString();

    }

}

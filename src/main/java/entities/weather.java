package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class weather {
    private Current current;
    @Getter
    @Setter
    public static class Current {
        public String observationTime;

        @JsonProperty("temperature")
        public int temperature;

        @JsonProperty("weather_descriptions")
        public ArrayList<String> weatherDescriptions;

        @JsonProperty("feelslike")
        private int feelslike; // now accessible via getter
    }
}

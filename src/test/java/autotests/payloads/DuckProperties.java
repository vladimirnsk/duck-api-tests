package autotests.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class DuckProperties {

    @JsonProperty("color")
    private String color;

    @JsonProperty("height")
    private double height;

    @JsonProperty("material")
    private String material;

    @JsonProperty("sound")
    private String sound;

    @JsonProperty("wingsState")
    private String wingsState;

}
package autotests.duck_controller;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckCreate extends TestNGCitrusSpringSupport {
    String baseURL = "http://localhost:2222";

    @Test(description = "Проверка создание уточки с материалом rubber")
    @CitrusTest
    public void createDuckMaterialRubber(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "Black", 1.11, "rubber", "quack", "ACTIVE");
        validateResponse(runner, "Black", 1.11, "rubber", "quack", "ACTIVE");
    }

    @Test(description = "Проверка создание уточки с материалом wood")
    @CitrusTest
    public void createDuckMaterialWood(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "While", 2.22, "wood", "quack", "FIXED");
        validateResponse(runner, "While", 2.22, "wood", "quack", "FIXED");
    }

    public void createDuck(TestCaseRunner runner, String color, double height, String material, String sound, String wingsState) {
        runner.$(
                http()
                        .client(baseURL)
                        .send()
                        .post("/api/duck/create")
                        .message()
                        .header("Content-Type", "application/json")
                        .body("{\n" +
                                "\"color\":\"" + color + "\",\n" +
                                "\"height\":" + height + ",\n" +
                                "\"material\":\"" + material + "\",\n" +
                                "\"sound\":\"" + sound + "\",\n" +
                                "\"wingsState\":\"" + wingsState + "\"\n" +
                                "}"
                        )
        );
    }

    public void validateResponse(TestCaseRunner runner, String expectedColor, double expectedHeight, String expectedMaterial, String expectedSound, String expectedWingsState) {
        runner.$(
                http()
                        .client(baseURL)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .extract(fromBody().expression("$.id", "duckId"))
                        .validate(jsonPath()
                                .expression("$.id", "@isNumber()@")
                                .expression("$.color", expectedColor)
                                .expression("$.height", String.valueOf(expectedHeight))
                                .expression("$.material", expectedMaterial)
                                .expression("$.sound", expectedSound)
                                .expression("$.wingsState", expectedWingsState))

        );
    }
}
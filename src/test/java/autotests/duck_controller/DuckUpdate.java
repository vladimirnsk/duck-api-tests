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
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckUpdate extends TestNGCitrusSpringSupport {
    String baseURL = "http://localhost:2222";

    @Test(description = "Проверка обновление параметров color и height для уточки")
    @CitrusTest
    public void updateDuckParametersColorHeight(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "Green", 4.44, "rubber", "quack", "ACTIVE");
        extractFromResponse(runner, "$.id", "duckId");

        updateDuck(runner, "${duckId}", "Black", 4.55, "rubber", "quack", "ACTIVE");
        validateResponse(runner, "{\n  \"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n}");
    }

    @Test(description = "Проверка обновление параметров color и sound для уточки")
    @CitrusTest
    public void updateDuckParametersColorSound(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "Blue", 5.55, "rubber", "quack", "ACTIVE");
        extractFromResponse(runner, "$.id", "duckId");

        updateDuck(runner, "${duckId}", "While", 5.55, "rubber", "quack-quack", "ACTIVE");
        validateResponse(runner, "{\n  \"message\": \"Duck with id = " + "${duckId}" + " is updated\"\n}");
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

    public void updateDuck(TestCaseRunner runner, String id, String changeColor, double changeHeight, String changeMaterial, String changeSound, String changeWingsState) {
        runner.$(
                http()
                        .client(baseURL)
                        .send()
                        .put("/api/duck/update")
                        .queryParam("id", id)
                        .queryParam("color", changeColor)
                        .queryParam("height", String.valueOf(changeHeight))
                        .queryParam("material", changeMaterial)
                        .queryParam("sound", changeSound)
                        .queryParam("wingsState", changeWingsState));
    }

    public void extractFromResponse(TestCaseRunner runner, String jsonPath, String variableName) {
        runner.$(
                http()
                        .client(baseURL)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)
                        .extract(fromBody().expression(jsonPath, variableName))
        );
    }


    public void validateResponse(TestCaseRunner runner, String responseMessage) {
        runner.$(
                http()
                        .client(baseURL)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(responseMessage)
        );
    }
}

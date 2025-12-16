package autotests.duck_action_controller;

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

public class DuckActionFly extends TestNGCitrusSpringSupport {
    String baseURL = "http://localhost:2222";

    @Test(description = "Проверка полета уточки с крыльями ACTIVE")
    @CitrusTest
    public void activeWingsFly(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "Green", 6.66, "iron", "quack", "ACTIVE");
        extractFromResponse(runner, "$.id", "duckId");

        duckFly(runner, "${duckId}");
        validateResponse(runner, "{\n \"message\": \"I am flying :)\"\n}");
    }

    @Test(description = "Проверка полета уточки с крыльями FIXED")
    @CitrusTest
    public void fixedWingsFly(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "Pink", 7.77, "wood", "quack", "FIXED");
        extractFromResponse(runner, "$.id", "duckId");

        duckFly(runner, "${duckId}");
        validateResponse(runner, "{\n \"message\": \"I can not fly :C\"\n}");
    }

    @Test(description = "Проверка полета уточки с крыльями UNDEFINED")
    @CitrusTest
    public void undefinedWingsFly(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "Yellow", 8.88, "rubber", "quack", "UNDEFINED");
        extractFromResponse(runner, "$.id", "duckId");

        duckFly(runner, "${duckId}");
        validateResponse(runner, "{\n \"message\": \"Wings are not detected :(\"\n}");
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

    public void duckFly(TestCaseRunner runner, String id) {
        runner.$(
                http()
                        .client(baseURL)
                        .send()
                        .get("/api/duck/action/fly")
                        .queryParam("id", id));
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

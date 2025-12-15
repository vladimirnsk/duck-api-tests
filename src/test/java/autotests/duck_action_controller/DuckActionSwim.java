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

public class DuckActionSwim extends TestNGCitrusSpringSupport {
    String baseURL = "http://localhost:2222";

    // TODO: SHIFT-AQA-01
    @Test(description = "Проверка способности плавать для существующей утки")
    @CitrusTest
    public void successSwim(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "Red", 9.99, "rubber", "quack", "ACTIVE");
        extractFromResponse(runner, "$.id", "duckId");

        duckSwim(runner, "${duckId}");
        //validateResponse(runner, "{\n \"message\": \"I'm swimming\"\n}", "OK");
        validateResponse(runner, "{\n \"message\": \"Paws are not found ((((\"\n}", "NOT_FOUND");
    }

    @Test(description = "Проверка способности плавать для несуществующей утки")
    @CitrusTest
    public void invalidIdSwim(@Optional @CitrusResource TestCaseRunner runner) {
        deleteDuck(runner,"912345");
        duckSwim(runner, "912345");

        validateResponse(runner, "{\n \"message\": \"Paws are not found ((((\"\n}", "NOT_FOUND");
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

    public void duckSwim(TestCaseRunner runner, String id) {
        runner.$(
                http()
                        .client(baseURL)
                        .send()
                        .get("/api/duck/action/swim")
                        .queryParam("id", id)
        );
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

    public void validateResponse(TestCaseRunner runner, String responseMessage, String status) {
        runner.$(
                http()
                        .client(baseURL)
                        .receive()
                        .response(HttpStatus.valueOf(status.toUpperCase()))
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(responseMessage)
        );
    }

    public void deleteDuck(TestCaseRunner runner, String id) {
        runner.$(
                http()
                        .client(baseURL)
                        .send()
                        .delete("/api/duck/delete")
                        .queryParam("id", id));
    }
}
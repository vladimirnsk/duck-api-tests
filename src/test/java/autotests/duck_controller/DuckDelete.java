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

public class DuckDelete extends TestNGCitrusSpringSupport {
    String baseURL = "http://localhost:2222";

    @Test(description = "Проверка удаление существующей уточки")
    @CitrusTest
    public void successDeleteDuck(@Optional @CitrusResource TestCaseRunner runner) {
        createDuck(runner, "Green", 3.33, "rubber", "quack", "ACTIVE");
        extractFromResponse(runner, "$.id", "duckId");
        deleteDuck(runner, "${duckId}");
        validateResponse(runner, "{\n \"message\": \"Duck is deleted\"\n}");
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

    public void deleteDuck(TestCaseRunner runner, String id) {
        runner.$(
                http()
                        .client(baseURL)
                        .send()
                        .delete("/api/duck/delete")
                        .queryParam("id", id));
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

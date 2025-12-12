package autotests.duck_action_controller;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.DelegatingPayloadVariableExtractor.Builder.fromBody;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;

public class DuckActionProperties extends TestNGCitrusSpringSupport {

    String baseURL = "http://localhost:2222";

    // TODO: SHIFT-AQA-03
    @Test(description = "Проверка свойств утки с четным ID и материалом wood")
    @CitrusTest
    public void verifyEvenIdWithWood(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        createDuckWithDesiredParity(runner, context, "Green", 11.11, "wood", "quack", "ACTIVE", true);
        requestProperties(runner, "${duckId}");

        //validateResponse(runner, "Green", 11110, "wood", "quack", "ACTIVE");
        validateResponse(runner);
    }

    @Test(description = "Проверка свойств утки с нечетным ID и материалом rubber")
    @CitrusTest
    public void verifyOddIdWithRubber(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        createDuckWithDesiredParity(runner, context, "Blue", 12.12, "rubber", "quack", "ACTIVE", false);
        requestProperties(runner, "${duckId}");

        validateResponse(runner, "Blue", 1212, "rubber", "quack", "ACTIVE");
    }

    public void createDuckWithDesiredParity(TestCaseRunner runner, TestContext context, String color, double height, String material, String sound, String wingsState, boolean shouldBeEven) {
        while (true) {
            createDuck(runner, color, height, material, sound, wingsState);
            extractFromResponse(runner, "$.id", "duckId");

            long idValue = Long.parseLong(context.getVariable("duckId"));
            boolean isEven = idValue % 2 == 0;

            if (shouldBeEven == isEven) {
                break;
            }
        }
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

    private void requestProperties(TestCaseRunner runner, String id) {
        runner.$(http()
                .client(baseURL)
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", id));
    }

    public void validateResponse(TestCaseRunner runner, String Color, double Height, String Material, String Sound, String WingsState) {
        runner.$(
                http()
                        .client(baseURL)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .validate(jsonPath()
                                .expression("$.color", Color)
                                .expression("$.height", String.valueOf(Height))
                                .expression("$.material", Material)
                                .expression("$.sound", Sound)
                                .expression("$.wingsState", WingsState)));
    }

    public void validateResponse(TestCaseRunner runner) {
        runner.$(
                http()
                        .client(baseURL)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(""));
    }
}
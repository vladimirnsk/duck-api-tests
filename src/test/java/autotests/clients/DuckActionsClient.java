package autotests.clients;

import autotests.EndpointConfig;
import autotests.payloads.DuckMessageResponse;
import autotests.payloads.DuckProperties;
import autotests.payloads.DuckSoundResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.MessageSupport;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.message.builder.ObjectMappingPayloadBuilder;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.consol.citrus.validation.json.JsonPathMessageValidationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@ContextConfiguration(classes = {EndpointConfig.class})
public class DuckActionsClient extends TestNGCitrusSpringSupport {

    @Autowired
    @Qualifier("duckService")
    protected HttpClient duckService;

    public void createDuck(TestCaseRunner runner, DuckProperties properties) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .post("/api/duck/create")
                        .message()
                        .header("Content-Type", "application/json")
                        .body(new ObjectMappingPayloadBuilder(properties, new ObjectMapper()))
        );
    }

    public void createDuckEnsuringIdParity(TestCaseRunner runner, TestContext context, DuckProperties properties, boolean shouldBeEven) {
        int maxAttempts = 5;
        int attempt = 0;

        while (attempt < maxAttempts) {
            attempt++;

            createDuck(runner, properties);
            extractFromResponse(runner, "$.id", "duckId");

            String idStr = context.getVariable("duckId");
            if (idStr == null || idStr.isEmpty()) {
                continue;
            }
            try {
                long idValue = Long.parseLong(idStr);
                boolean isEven = idValue % 2 == 0;

                if (shouldBeEven == isEven) {
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid ID format on attempt " + attempt + ": " + e.getMessage());
            }
        }

        throw new RuntimeException("Failed to create duck with desired ID parity after " + maxAttempts + " attempts. Last ID: " + context.getVariable("duckId"));
    }

    public void updateDuck(TestCaseRunner runner, String id, DuckProperties properties) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .put("/api/duck/update")
                        .queryParam("id", id)
                        .queryParam("color", properties.color())
                        .queryParam("height", String.valueOf(properties.height()))
                        .queryParam("material", properties.material())
                        .queryParam("sound", properties.sound())
                        .queryParam("wingsState", properties.wingsState())
        );
    }

    public void deleteDuck(TestCaseRunner runner, String id) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .delete("/api/duck/delete")
                        .queryParam("id", id)
        );
    }

    public void duckFly(TestCaseRunner runner, String id) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .get("/api/duck/action/fly")
                        .queryParam("id", id)
        );
    }

    public void duckProperties(TestCaseRunner runner, String id) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .get("/api/duck/action/properties")
                        .queryParam("id", id));
    }

    public void duckQuack(TestCaseRunner runner, String id, int repetitionCount, int soundCount) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .get("/api/duck/action/quack")
                        .queryParam("id", id)
                        .queryParam("repetitionCount", String.valueOf(repetitionCount))
                        .queryParam("soundCount", String.valueOf(soundCount))
        );
    }

    public void duckSwim(TestCaseRunner runner, String id) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .get("/api/duck/action/swim")
                        .queryParam("id", id)
        );
    }

    public void extractFromResponse(TestCaseRunner runner, String jsonPath, String variableName) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .type(MessageType.JSON)
                        .extract(MessageSupport.MessageBodySupport.fromBody().expression(jsonPath, variableName))
        );
    }

    public void validateFullResponse(TestCaseRunner runner, DuckProperties properties) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .validate(JsonPathMessageValidationContext.Builder.jsonPath()
                                .expression("$.id", "@isNumber()@")
                                .expression("$.color", properties.color())
                                .expression("$.height", String.valueOf(properties.height()))
                                .expression("$.material", properties.material())
                                .expression("$.sound", properties.sound())
                                .expression("$.wingsState", properties.wingsState())));
    }

    public void validateFullResponse(TestCaseRunner runner, String Color, double Height, String Material, String Sound, String WingsState) {
        runner.$(
                http()
                        .client(duckService)
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

    public void validateResponse(TestCaseRunner runner, DuckMessageResponse Message, String status) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.valueOf(status.toUpperCase()))
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(new ObjectMappingPayloadBuilder(Message, new ObjectMapper()))
        );
    }

    public void validateResponse(TestCaseRunner runner) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(""));
    }

    public void validateResponseSound(TestCaseRunner runner, DuckSoundResponse soundMessage,
                                      int repetitionCount, int soundCount) {

        String baseSound = soundMessage.sound();
        String singleSound = String.join("-", Collections.nCopies(repetitionCount, baseSound));
        String expectedSound = String.join(", ", Collections.nCopies(soundCount, singleSound));

        DuckSoundResponse expectedResponse = new DuckSoundResponse().sound(expectedSound);

        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(new ObjectMappingPayloadBuilder(expectedResponse, new ObjectMapper()))
        );
    }
}
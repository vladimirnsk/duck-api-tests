package autotests;

import autotests.payloads.DuckMessageResponse;
import autotests.payloads.DuckProperties;
import autotests.payloads.DuckSoundResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.dsl.MessageSupport;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.builder.ObjectMappingPayloadBuilder;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.consol.citrus.validation.json.JsonPathMessageValidationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;


import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@ContextConfiguration(classes = {EndpointConfig.class})
public class BaseTest extends TestNGCitrusSpringSupport {

    @Autowired
    @Qualifier("duckService")
    protected HttpClient duckService;

    @Autowired
    protected SingleConnectionDataSource testDb;

    protected void sendPostRequestCreate(TestCaseRunner runner, String path, Object payload) {
        runner.$(http()
                .client(duckService)
                .send()
                .post(path)
                .message()
                .header("Content-Type", "application/json")
                .body(new ObjectMappingPayloadBuilder(payload, new ObjectMapper()))
        );
    }

    protected void sendGetRequestId(TestCaseRunner runner, String path, String id) {
        runner.$(http()
                .client(duckService)
                .send()
                .get(path)
                .queryParam("id", id)
        );
    }

    protected void sendGetRequestQuack(TestCaseRunner runner, String path, String id, int repetitionCount, int soundCount) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .get(path)
                        .queryParam("id", id)
                        .queryParam("repetitionCount", String.valueOf(repetitionCount))
                        .queryParam("soundCount", String.valueOf(soundCount))
        );
    }

    protected void sendPutRequestUpdate(TestCaseRunner runner, String path, String id, DuckProperties properties) {
        runner.$(
                http()
                        .client(duckService)
                        .send()
                        .put(path)
                        .queryParam("id", id)
                        .queryParam("color", properties.color())
                        .queryParam("height", String.valueOf(properties.height()))
                        .queryParam("material", properties.material())
                        .queryParam("sound", properties.sound())
                        .queryParam("wingsState", properties.wingsState())
        );
    }

    protected void sendDeleteRequestId(TestCaseRunner runner, String path, String id) {
        runner.$(http()
                .client(duckService)
                .send()
                .delete(path)
                .queryParam("id", id)
        );
    }

    protected void extractFromResponseValue(TestCaseRunner runner, String jsonPath, String variableName) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(MessageSupport.MessageBodySupport.fromBody().expression(jsonPath, variableName))
        );
    }

    protected void validateFullResponseValue(TestCaseRunner runner, DuckProperties properties) {
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

    protected void validateFullResponseValue(TestCaseRunner runner, String Color, double Height, String Material, String Sound, String WingsState) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .validate(JsonPathMessageValidationContext.Builder.jsonPath()
                                .expression("$.color", Color)
                                .expression("$.height", String.valueOf(Height))
                                .expression("$.material", Material)
                                .expression("$.sound", Sound)
                                .expression("$.wingsState", WingsState)));
    }

    protected void validateClearResponseValue(TestCaseRunner runner) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(""));
    }

    protected void validateResponseResourcesValue(TestCaseRunner runner, String expectedPayload) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(new ClassPathResource(expectedPayload)));
    }

    protected void validateResponsePayloadValue(TestCaseRunner runner, DuckMessageResponse Message, String status) {
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

    protected void validateResponseSoundValue(TestCaseRunner runner, DuckSoundResponse expectedResponse) {
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
package autotests;

import autotests.payloads.DuckProperties;
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

    protected void sendPostRequest(TestCaseRunner runner, String path, Object payload) {
        runner.$(http()
                .client(duckService)
                .send()
                .post(path)
                .message()
                .header("Content-Type", "application/json")
                .body(new ObjectMappingPayloadBuilder(payload, new ObjectMapper()))
        );
    }

    protected void sendGetRequest(TestCaseRunner runner, HttpClient URL, String path) {
        runner.$(http()
                .client(URL)
                .send()
                .get(path));
    }

    protected void sendPutRequest(TestCaseRunner runner, HttpClient URL, String path) {
        runner.$(http()
                .client(URL)
                .send()
                .put(path));
    }

    protected void sendDeleteRequest(TestCaseRunner runner, HttpClient URL, String path) {
        runner.$(http()
                .client(URL)
                .send()
                .delete(path));
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

    protected void validateResponsePayloadValue(TestCaseRunner runner, DuckProperties properties) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .validate(JsonPathMessageValidationContext.Builder.jsonPath()
                                .expression("$.id", "${duckId}")
                                .expression("$.color", properties.color())
                                .expression("$.height", String.valueOf(properties.height()))
                                .expression("$.material", properties.material())
                                .expression("$.sound", properties.sound())
                                .expression("$.wingsState", properties.wingsState())));
    }

    protected void validatePropertiesResponseValue(TestCaseRunner runner, String Color, double Height, String Material, String Sound, String WingsState) {
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

    protected void validateEmptyResponseValue(TestCaseRunner runner) {
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

    protected <T> void validateResponse(TestCaseRunner runner, T expectedResponse, String status) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.valueOf(status.toUpperCase()))
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(new ObjectMappingPayloadBuilder(expectedResponse, new ObjectMapper()))
        );
    }
}
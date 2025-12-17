package autotests.clients;

import autotests.BaseTest;
import autotests.payloads.DuckMessageResponse;
import autotests.payloads.DuckProperties;
import autotests.payloads.DuckSoundResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.context.TestContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Locale;

import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;
import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


public class DuckActionsClient extends BaseTest {

    @Step("Создать уточку")
    public void createDuck(TestCaseRunner runner, DuckProperties properties) {
        sendPostRequest(runner, "/api/duck/create", properties);
    }

    @Step("Создать уточку SQL")
    public void createDuckDB(TestCaseRunner runner, DuckProperties properties) {
        String insertSql = String.format(
                Locale.US,
                "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE) " +
                        "VALUES (${duckId}, '%s', %.2f, '%s', '%s', '%s')",
                properties.color(),
                properties.height(),
                properties.material(),
                properties.sound(),
                properties.wingsState()
        );

        runner.$(sql(testDb)
                .statement(insertSql));
    }

    @Step("Создать уточку в зависимости от четности")
    public void createDuckEnsuringIdParity(TestCaseRunner runner, TestContext context, DuckProperties properties, boolean shouldBeEven) {

        String idStr = context.getVariable("duckId");
        if (idStr == null || idStr.isEmpty()) {
            runner.variable("duckId", "citrus:randomNumber(5)");
            idStr = context.getVariable("duckId");
        }

        try {
            long idValue = Long.parseLong(idStr);
            boolean isEven = idValue % 2 == 0;

            if (shouldBeEven && !isEven) {
                idValue += 1;
            } else if (!shouldBeEven && isEven) {
                idValue += 1;
            }
            context.setVariable("duckId", String.valueOf(idValue));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid ID format: " + idStr, e);
        }

        createDuckDB(runner, properties);
    }

    @Step("Обновить характеристики уточки")
    public void updateDuck(TestCaseRunner runner, String id, DuckProperties properties) {
        String path = "/api/duck/update?id=" + id +
                "&color=" + properties.color() +
                "&height=" + properties.height() +
                "&material=" + properties.material() +
                "&sound=" + properties.sound() +
                "&wingsState=" + properties.wingsState();
        sendPutRequest(runner, duckService, path);
    }

    @Step("Обновить характеристики уточки")
    public void updateDuckByIdDB(TestCaseRunner runner, DuckProperties newProperties) {
        String updateSql = String.format(
                Locale.US,
                "UPDATE DUCK SET " +
                        "COLOR = '%s', " +
                        "HEIGHT = %.6f, " +
                        "MATERIAL = '%s', " +
                        "SOUND = '%s', " +
                        "WINGS_STATE = '%s' " +
                        "WHERE ID = ${duckId}",
                newProperties.color(),
                newProperties.height(),
                newProperties.material(),
                newProperties.sound(),
                newProperties.wingsState()
        );

        runner.$(sql(testDb)
                .statement(updateSql));
    }

    @Step("Удалить уточку")
    public void deleteDuck(TestCaseRunner runner, String id) {
        sendGetRequest(runner, duckService, "/api/duck/delete" + "?id=" + id);
    }

    @Step("Удалить уточку по характеристикам SQL")
    public void deleteDuckByPropertiesDB(TestCaseRunner runner, DuckProperties properties) {
        String deleteSql = String.format(
                Locale.US,
                "DELETE FROM DUCK WHERE " +
                        "COLOR = '%s' AND " +
                        "HEIGHT = %.6f AND " +
                        "MATERIAL = '%s' AND " +
                        "SOUND = '%s' AND " +
                        "WINGS_STATE = '%s'",
                properties.color(),
                properties.height(),
                properties.material(),
                properties.sound(),
                properties.wingsState()
        );

        runner.$(sql(testDb)
                .statement(deleteSql));
    }

    @Step("Удалить уточку по ID SQL")
    public void deleteDuckByIdDB(TestCaseRunner runner) {
        runner.$(sql(testDb)
                .statement("DELETE FROM DUCK WHERE ID = ${duckId}"));

        runner.$(query(testDb)
                .statement("SELECT COUNT(*) FROM DUCK WHERE ID = ${duckId}")
                .validate("COUNT(*)", "0"));
    }

    @Step("Уточка летит")
    public void duckFly(TestCaseRunner runner, String id) {
        sendGetRequest(runner, duckService, "/api/duck/action/fly" + "?id=" + id);
    }

    @Step("Получить характеристики уточки")
    public void duckProperties(TestCaseRunner runner, String id) {
        sendGetRequest(runner, duckService, "/api/duck/action/properties" + "?id=" + id);
    }

    @Step("Уточка крякает")
    public void duckQuack(TestCaseRunner runner, String id, int repetitionCount, int soundCount) {
        sendGetRequest(runner, duckService, "/api/duck/action/quack" + "?id=" + id + "&repetitionCount="
                + String.valueOf(repetitionCount) + "&soundCount=" + String.valueOf(soundCount) );
    }

    @Step("Уточка плывет")
    public void duckSwim(TestCaseRunner runner, String id) {
        sendGetRequest(runner, duckService, "/api/duck/action/swim" + "?id=" + id);
    }

    @Step("Получить необходимые данные из ответа")
    public void extractFromResponse(TestCaseRunner runner, String jsonPath, String variableName) {
        extractFromResponseValue(runner, jsonPath, variableName);
    }

    @Step("Валидировать по свойствам утки payload")
    public void validateFullResponse(TestCaseRunner runner, DuckProperties properties) {
        validateFullResponseValue(runner, properties);
    }

    @Step("Валидировать по свойствам утки string")
    public void validateFullResponse(TestCaseRunner runner, String Color, double Height, String Material, String Sound, String WingsState) {
        validateFullResponseValue(runner, Color, Height, Material, Sound, WingsState);
    }

    @Step("Валидировать пустой ответ")
    public void validateResponse(TestCaseRunner runner) {
        validateClearResponseValue(runner);
    }

    @Step("Валидировать при передачи строки сообщения")
    public void validateResponseString(TestCaseRunner runner, String responseMessage, String status) {
        runner.$(
                http()
                        .client(duckService)
                        .receive()
                        .response(HttpStatus.valueOf(status.toUpperCase()))
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(responseMessage)
        );
    }

    @Step("Валидировать при передачи ресурса")
    public void validateResponseResources(TestCaseRunner runner, String expectedPayload) {
        validateResponseResourcesValue(runner, expectedPayload);
    }

    @Step("Валидировать при передачи сообщения")
    public void validateResponsePayload(TestCaseRunner runner, DuckMessageResponse Message, String status) {
        validateResponsePayloadValue(runner, Message, status);
    }

    @Step("Валидировать при передачи звука")
    public void validateResponseSound(TestCaseRunner runner, DuckSoundResponse soundMessage,
                                      int repetitionCount, int soundCount) {

        String baseSound = soundMessage.sound();
        String singleSound = String.join("-", Collections.nCopies(repetitionCount, baseSound));
        String expectedSound = String.join(", ", Collections.nCopies(soundCount, singleSound));

        DuckSoundResponse expectedResponse = new DuckSoundResponse().sound(expectedSound);
        validateResponseSoundValue(runner, expectedResponse);
    }

    @Step("Найти уточку по характеристикам SQL")
    public void findDuckByPropertiesDB(TestCaseRunner runner, DuckProperties properties) {
        String searchSql = String.format(
                Locale.US,
                "SELECT COUNT(*) FROM DUCK WHERE " +
                        "ID = %s AND " +
                        "COLOR = '%s' AND " +
                        "HEIGHT = %.6f AND " +
                        "MATERIAL = '%s' AND " +
                        "SOUND = '%s' AND " +
                        "WINGS_STATE = '%s'",
                "${duckId}",
                properties.color(),
                properties.height(),
                properties.material(),
                properties.sound(),
                properties.wingsState()
        );

        runner.$(query(testDb)
                .statement(searchSql));
    }

    @Step("Валидировать JSON для SQL ")
    public void validateResponseResourcesDB(TestCaseRunner runner, String expectedPayloadPath) throws Exception {
        ClassPathResource resource = new ClassPathResource(expectedPayloadPath);
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DuckProperties expectedDuck = mapper.readValue(resource.getInputStream(), DuckProperties.class);

        validateDuckInDBByProperties(runner, expectedDuck);
    }

    @Step("Валидировать уточку в БД по характеристикам")
    public void validateDuckInDBByProperties(TestCaseRunner runner, DuckProperties expectedDuck) {
        String validateSql = String.format(
                Locale.US,
                "SELECT COUNT(*) FROM DUCK WHERE " +
                        "ID = ${duckId} AND " +
                        "COLOR = '%s' AND " +
                        "HEIGHT = %.6f AND " +
                        "MATERIAL = '%s' AND " +
                        "SOUND = '%s' AND " +
                        "WINGS_STATE = '%s'",
                expectedDuck.color(),
                expectedDuck.height(),
                expectedDuck.material(),
                expectedDuck.sound(),
                expectedDuck.wingsState()
        );

        runner.$(query(testDb)
                .statement(validateSql)
                .validate("COUNT(*)", "1"));
    }
}
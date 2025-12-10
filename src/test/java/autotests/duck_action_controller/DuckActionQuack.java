package autotests.duck_action_controller;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.DelegatingPayloadVariableExtractor.Builder.fromBody;

public class DuckActionQuack extends TestNGCitrusSpringSupport {
    private static final String BASE_URL = "http://localhost:2222";
    private static final int REPETITION_COUNT = 3;
    private static final int SOUND_COUNT = 4;
    private String duckId;

    @Test(description = "Проверка способности крякать при четном ID")
    @CitrusTest
    public void quackDuckWithEvenId(@Optional @CitrusResource TestCaseRunner runner) {
        //String quackSound = "quack";
        String quackSound = "moo";
        String[] duckData = createDuckWithIdParity(runner, true, quackSound);
        quackDuck(runner, duckData[0], REPETITION_COUNT, SOUND_COUNT);
        validateResponse(runner, duckData[1], REPETITION_COUNT, SOUND_COUNT);
    }

    @Test(description = "Проверка способности крякать при нечетном ID")
    @CitrusTest
    public void quackDuckWithOddId(@Optional @CitrusResource TestCaseRunner runner) {
        String quackSound = "quack";
        String[] duckData = createDuckWithIdParity(runner, false, quackSound);
        quackDuck(runner, duckData[0], REPETITION_COUNT, SOUND_COUNT);
        validateResponse(runner, duckData[1], REPETITION_COUNT, SOUND_COUNT);
    }

    public void createDuck(TestCaseRunner runner, String color, double height, String material, String sound, String wingsState) {
        runner.$(
                http()
                        .client(BASE_URL)
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

    private String getCreatedDuckId(TestCaseRunner runner) {
        final String[] duckIdHolder = new String[1];
        runner.$(
                http()
                        .client(BASE_URL)
                        .receive()
                        .response()
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .extract(fromBody().expression("$.id", "id"))
        );
        runner.run(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                duckIdHolder[0] = context.getVariable("id");
            }
        });
        return duckIdHolder[0];
    }

    private boolean isEvenId(String id) {
        return Integer.parseInt(id) % 2 == 0;
    }

    private String[] createDuckWithIdParity(TestCaseRunner runner, boolean shouldBeEven, String quackSound) {
        boolean duckCreatedWithDesiredParity;
        do {
            createDuck(runner, "Black", 10.0, "iron", quackSound, "ACTIVE");
            duckId = getCreatedDuckId(runner);
            duckCreatedWithDesiredParity = isEvenId(duckId) == shouldBeEven;
        } while (!duckCreatedWithDesiredParity);
        return new String[]{duckId, quackSound};
    }

    private void quackDuck(TestCaseRunner runner, String id, int repetitionCount, int soundCount) {
        runner.$(
                http()
                        .client(BASE_URL)
                        .send()
                        .get("/api/duck/action/quack")
                        .queryParam("id", id)
                        .queryParam("repetitionCount", String.valueOf(repetitionCount))
                        .queryParam("soundCount", String.valueOf(soundCount))
        );
    }

    private String generateSingleDuckSound(String duckSound, int repetitionCount) {
        StringBuilder soundBuilder = new StringBuilder();
        for (int i = 0; i < repetitionCount; i++) {
            if (i > 0) {
                soundBuilder.append("-");
            }
            soundBuilder.append(duckSound);
        }
        return soundBuilder.toString();
    }

    private String buildExpectedSoundMessage(String duckSound, int repetitionCount, int soundCount) {
        StringBuilder expectedMessageBuilder = new StringBuilder();
        String singleSound = generateSingleDuckSound(duckSound, repetitionCount);
        for (int i = 0; i < soundCount; i++) {
            if (i > 0) {
                expectedMessageBuilder.append(", ");
            }
            expectedMessageBuilder.append(singleSound);
        }
        return expectedMessageBuilder.toString();
    }

    private void validateResponse(TestCaseRunner runner, String duckSound, int repetitionCount, int soundCount) {
        String expectedSound = buildExpectedSoundMessage(duckSound, repetitionCount, soundCount);
        String expectedResponse = String.format("{\n \"sound\": \"%s\"\n}", expectedSound);
        runner.$(
                http()
                        .client(BASE_URL)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(expectedResponse)
        );
    }
}
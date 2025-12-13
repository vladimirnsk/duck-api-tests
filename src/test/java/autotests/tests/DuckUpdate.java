package autotests.tests;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckMessageResponse;
import autotests.payloads.DuckProperties;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;

import org.testng.annotations.Optional;
import org.testng.annotations.Test;


public class DuckUpdate extends DuckActionsClient {

    @Test(description = "Проверка обновление параметров color и height для уточки")
    @CitrusTest
    public void updateDuckParametersColorHeight(@Optional @CitrusResource TestCaseRunner runner) {
        DuckProperties duckUpdateColorHeight = new DuckProperties()
                .color("Green")
                .height(4.44)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuck(runner, duckUpdateColorHeight);
        extractFromResponse(runner, "$.id", "duckId");

        duckUpdateColorHeight
                .color("Black")
                .height(4.55);

        updateDuck(runner, "${duckId}", duckUpdateColorHeight);

        DuckMessageResponse messageResponse = new DuckMessageResponse().message("Duck with id = ${duckId} is updated");
        validateResponsePayload(runner, messageResponse, "OK");
    }

    @Test(description = "Проверка обновление параметров color и sound для уточки")
    @CitrusTest
    public void updateDuckParametersColorSound(@Optional @CitrusResource TestCaseRunner runner) {
        DuckProperties duckUpdateColorSound = new DuckProperties()
                .color("Blue")
                .height(5.55)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuck(runner, duckUpdateColorSound);
        extractFromResponse(runner, "$.id", "duckId");

        duckUpdateColorSound
                .color("While")
                .sound("quack-quack");

        updateDuck(runner, "${duckId}", duckUpdateColorSound);

        DuckMessageResponse messageResponse = new DuckMessageResponse().message("Duck with id = ${duckId} is updated");
        validateResponsePayload(runner, messageResponse, "OK");
    }
}
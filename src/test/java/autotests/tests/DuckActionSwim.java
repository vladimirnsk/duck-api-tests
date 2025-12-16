package autotests.tests;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckMessageResponse;
import autotests.payloads.DuckProperties;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;


public class DuckActionSwim extends DuckActionsClient {

    // TODO: SHIFT-AQA-01
    @Test(description = "Проверка способности плавать для существующей утки")
    @CitrusTest
    public void successSwim(@Optional @CitrusResource TestCaseRunner runner) {
        DuckProperties duckSwim = new DuckProperties()
                .color("Red")
                .height(9.99)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuck(runner, duckSwim);
        extractFromResponse(runner, "$.id", "duckId");

        duckSwim(runner, "${duckId}");
        DuckMessageResponse messageResponse = new DuckMessageResponse().message("Paws are not found ((((");
        //validateResponse(runner, messageResponse,"OK");
        validateResponsePayload(runner, messageResponse, "NOT_FOUND");
    }

    @Test(description = "Проверка способности плавать для несуществующей утки")
    @CitrusTest
    public void invalidIdSwim(@Optional @CitrusResource TestCaseRunner runner) {
        DuckProperties duckSwim = new DuckProperties()
                .color("Red")
                .height(9.99)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuck(runner, duckSwim);
        extractFromResponse(runner, "$.id", "duckId");

        deleteDuck(runner,"${duckId}");
        duckSwim(runner, "${duckId}");

        DuckMessageResponse messageResponse = new DuckMessageResponse().message("Paws are not found ((((");
        validateResponsePayload(runner, messageResponse, "NOT_FOUND");
    }
}
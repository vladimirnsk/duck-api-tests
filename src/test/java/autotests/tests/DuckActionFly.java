package autotests.tests;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckMessageResponse;
import autotests.payloads.DuckProperties;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;


public class DuckActionFly extends DuckActionsClient {

    @Test(description = "Проверка полета уточки с крыльями ACTIVE")
    @CitrusTest
    public void activeWingsFly(@Optional @CitrusResource TestCaseRunner runner) {
        DuckProperties duckFlyActive = new DuckProperties()
                .color("Green")
                .height(6.66)
                .material("iron")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuck(runner, duckFlyActive);
        extractFromResponse(runner, "$.id", "duckId");

        duckFly(runner, "${duckId}");
        DuckMessageResponse messageResponse = new DuckMessageResponse().message("I am flying :)");
        validateResponse(runner, messageResponse, "OK");
    }

    @Test(description = "Проверка полета уточки с крыльями FIXED")
    @CitrusTest
    public void fixedWingsFly(@Optional @CitrusResource TestCaseRunner runner) {
        DuckProperties duckFlyFixed = new DuckProperties()
                .color("Pink")
                .height(7.77)
                .material("wood")
                .sound("quack")
                .wingsState("FIXED");

        createDuck(runner, duckFlyFixed);
        extractFromResponse(runner, "$.id", "duckId");

        duckFly(runner, "${duckId}");
        DuckMessageResponse messageResponse = new DuckMessageResponse().message("I can not fly :C");
        validateResponse(runner, messageResponse, "OK");
    }

    @Test(description = "Проверка полета уточки с крыльями UNDEFINED")
    @CitrusTest
    public void undefinedWingsFly(@Optional @CitrusResource TestCaseRunner runner) {
        DuckProperties duckFlyUndefined = new DuckProperties()
                .color("Yellow")
                .height(8.88)
                .material("rubber")
                .sound("quack")
                .wingsState("UNDEFINED");

        createDuck(runner, duckFlyUndefined);
        extractFromResponse(runner, "$.id", "duckId");

        duckFly(runner, "${duckId}");
        DuckMessageResponse messageResponse = new DuckMessageResponse().message("Wings are not detected :(");
        validateResponse(runner, messageResponse, "OK");
    }
}
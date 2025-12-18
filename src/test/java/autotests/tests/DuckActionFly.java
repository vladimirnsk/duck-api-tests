package autotests.tests;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckMessageResponse;
import autotests.payloads.DuckProperties;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Тесты на duck-actions-controller")
@Feature("Полет уточки")
@Story("Эндпоинт /api/duck/action/fly")
public class DuckActionFly extends DuckActionsClient {

    @Test(description = "Проверка полета уточки с крыльями ACTIVE")
    @CitrusTest
    public void activeWingsFly(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("duckId", "citrus:randomNumber(5,false)");
        DuckProperties duckFlyActive = new DuckProperties()
                .color("Green")
                .height(6.66)
                .material("iron")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuckDB(runner, duckFlyActive);

        duckFly(runner, "${duckId}");

        DuckMessageResponse messageResponse = new DuckMessageResponse().message("I am flying :)");
        validateResponseMessage(runner, messageResponse, "OK");
    }

    @Test(description = "Проверка полета уточки с крыльями FIXED")
    @CitrusTest
    public void fixedWingsFly(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("duckId", "citrus:randomNumber(5,false)");
        DuckProperties duckFlyFixed = new DuckProperties()
                .color("Pink")
                .height(7.77)
                .material("wood")
                .sound("quack")
                .wingsState("FIXED");

        createDuckDB(runner, duckFlyFixed);

        duckFly(runner, "${duckId}");

        DuckMessageResponse messageResponse = new DuckMessageResponse().message("I can not fly :C");
        validateResponseMessage(runner, messageResponse, "OK");
    }

    @Test(description = "Проверка полета уточки с крыльями UNDEFINED")
    @CitrusTest
    public void undefinedWingsFly(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("duckId", "citrus:randomNumber(5,false)");
        DuckProperties duckFlyUndefined = new DuckProperties()
                .color("Yellow")
                .height(8.88)
                .material("rubber")
                .sound("quack")
                .wingsState("UNDEFINED");

        createDuckDB(runner, duckFlyUndefined);

        duckFly(runner, "${duckId}");
        validateResponseString(runner, "{\n \"message\": \"Wings are not detected :(\"\n}", "OK");
    }
}
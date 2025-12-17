package autotests.tests;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckProperties;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Тесты на duck-controller")
@Feature("Удаление уточки")
@Story("Эндпоинт /api/duck/delete")
public class DuckDelete extends DuckActionsClient {

    @Test(description = "Проверка удаление существующей уточки")
    @CitrusTest
    public void successDeleteDuck(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("duckId","citrus:randomNumber(5)");
        DuckProperties duckPropertiesDelete = new DuckProperties()
                .color("Green")
                .height(3.33)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuckDB(runner, duckPropertiesDelete);
        deleteDuckByIdDB(runner);
    }
}
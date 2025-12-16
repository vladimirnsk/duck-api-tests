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
@Feature("Обновление характеристик уточки")
@Story("Эндпоинт /api/duck/update")
public class DuckUpdate extends DuckActionsClient {

    @Test(description = "Проверка обновление параметров color и height для уточки")
    @CitrusTest
    public void updateDuckParametersColorHeight(@Optional @CitrusResource TestCaseRunner runner) {
        clearDuckTable(runner);
        DuckProperties duckOriginColorHeight = new DuckProperties()
                .color("Green")
                .height(4.44)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuckDB(runner, duckOriginColorHeight);
        findDuckByPropertiesDB(runner, duckOriginColorHeight);

        DuckProperties duckUpdateColorHeight = new DuckProperties()
                .color("Black")
                .height(4.55)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        updateDuck(runner, "${duckId}", duckUpdateColorHeight);
        findDuckByPropertiesDB(runner, duckUpdateColorHeight);
    }

    @Test(description = "Проверка обновление параметров color и sound для уточки")
    @CitrusTest
    public void updateDuckParametersColorSound(@Optional @CitrusResource TestCaseRunner runner) {
        clearDuckTable(runner);
        DuckProperties duckOriginColorSound = new DuckProperties()
                .color("Blue")
                .height(5.55)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuckDB(runner, duckOriginColorSound);
        findDuckByPropertiesDB(runner, duckOriginColorSound);

        DuckProperties duckUpdateColorSound = new DuckProperties()
                .color("While")
                .height(5.55)
                .material("rubber")
                .sound("quack-quack")
                .wingsState("ACTIVE");

        updateDuck(runner, "${duckId}", duckUpdateColorSound);
        findDuckByPropertiesDB(runner, duckUpdateColorSound);
    }
}
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
        DuckProperties duckUpdateColorHeight = new DuckProperties()
                .color("Green")
                .height(4.44)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuckDB(runner, duckUpdateColorHeight);
        findDuckByPropertiesDB(runner, duckUpdateColorHeight);

        duckUpdateColorHeight
                .color("Black")
                .height(4.55);

        updateDuckByIdDB(runner, duckUpdateColorHeight);
        findDuckByPropertiesDB(runner, duckUpdateColorHeight);
        deleteDuckByIdDB(runner);
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

        createDuckDB(runner, duckUpdateColorSound);
        findDuckByPropertiesDB(runner, duckUpdateColorSound);

        duckUpdateColorSound
                .color("While")
                .sound("quack-quack");

        updateDuckByIdDB(runner, duckUpdateColorSound);
        findDuckByPropertiesDB(runner, duckUpdateColorSound);
        deleteDuckByIdDB(runner);
    }
}
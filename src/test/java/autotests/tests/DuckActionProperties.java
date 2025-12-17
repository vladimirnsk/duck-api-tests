package autotests.tests;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckProperties;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Тесты на duck-actions-controller")
@Feature("Просмотр характеристик уточки")
@Story("Эндпоинт /api/duck/action/properties")
public class DuckActionProperties extends DuckActionsClient {

    // TODO: SHIFT-AQA-03
    @Test(description = "Проверка свойств утки с четным ID и материалом wood")
    @CitrusTest
    public void verifyEvenIdWithWood(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        clearDuckTable(runner);
        DuckProperties duckPropertiesEven= new DuckProperties()
                .color("Green")
                .height(11.11)
                .material("wood")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuckEnsuringIdParity(runner, context, duckPropertiesEven, true);
        duckProperties(runner, "${duckId}");

        //validateFullResponse(runner, "Green", 11110, "wood", "quack", "ACTIVE");
        validateResponse(runner);
    }

    @Test(description = "Проверка свойств утки с нечетным ID и материалом rubber")
    @CitrusTest
    public void verifyOddIdWithRubber(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        clearDuckTable(runner);
        DuckProperties duckPropertiesOdd = new DuckProperties()
                .color("Blue")
                .height(12.12)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuckEnsuringIdParity(runner, context, duckPropertiesOdd, false);
        duckProperties(runner, "${duckId}");

        validateFullResponse(runner, "Blue", 1212, "rubber", "quack", "ACTIVE");
    }
}
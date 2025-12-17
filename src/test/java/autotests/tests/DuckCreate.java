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
@Feature("Параметризированное создание уточки")
@Story("Эндпоинт /api/duck/create")
public class DuckCreate extends DuckActionsClient {

    @Test(description = "Проверка создание уточки с материалом rubber")
    @CitrusTest
    public void createDuckMaterialRubber(@Optional @CitrusResource TestCaseRunner runner) throws Exception {
        runner.variable("duckId","citrus:randomNumber(5)");
        DuckProperties duckCreateRubber = new DuckProperties()
                .color("Black")
                .height(1.11)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuckDB(runner, duckCreateRubber);
        validateResponseResourcesDB(runner, "duckCreateTest/createMaterialRubber.json");
    }

    @Test(description = "Проверка создание уточки с материалом wood")
    @CitrusTest
    public void createDuckMaterialWood(@Optional @CitrusResource TestCaseRunner runner) throws Exception {
        runner.variable("duckId","citrus:randomNumber(5)");
        DuckProperties duckCreateWood = new DuckProperties()
                .color("While")
                .height(2.22)
                .material("wood")
                .sound("quack")
                .wingsState("FIXED");

        createDuckDB(runner, duckCreateWood);
        validateResponseResourcesDB(runner, "duckCreateTest/createMaterialWood.json");
    }

    @Test(description = "Проверка создание уточки с материалом iron")
    @CitrusTest
    public void createDuckMaterialIron(@Optional @CitrusResource TestCaseRunner runner) {
        runner.variable("duckId","citrus:randomNumber(5)");
        DuckProperties duckCreateIron = new DuckProperties()
                .color("Blue")
                .height(3.34)
                .material("iron")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuckDB(runner, duckCreateIron);
        findDuckByPropertiesDB(runner, duckCreateIron);
    }
}
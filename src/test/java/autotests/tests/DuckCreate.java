package autotests.tests;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckProperties;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;

import org.testng.annotations.Optional;
import org.testng.annotations.Test;


public class DuckCreate extends DuckActionsClient {

    @Test(description = "Проверка создание уточки с материалом rubber")
    @CitrusTest
    public void createDuckMaterialRubber(@Optional @CitrusResource TestCaseRunner runner) {
        DuckProperties duckCreateRubber = new DuckProperties()
                .color("Black")
                .height(1.11)
                .material("rubber")
                .sound("quack")
                .wingsState("ACTIVE");

        createDuck(runner, duckCreateRubber);
        validateFullResponse(runner, duckCreateRubber);
    }

    @Test(description = "Проверка создание уточки с материалом wood")
    @CitrusTest
    public void createDuckMaterialWood(@Optional @CitrusResource TestCaseRunner runner) {
        DuckProperties duckCreateWood = new DuckProperties()
                .color("While")
                .height(2.22)
                .material("wood")
                .sound("quack")
                .wingsState("FIXED");

        createDuck(runner, duckCreateWood);
        validateFullResponse(runner, duckCreateWood);
    }
}
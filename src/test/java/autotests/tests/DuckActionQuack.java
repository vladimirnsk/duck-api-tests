package autotests.tests;

import autotests.clients.DuckActionsClient;
import autotests.payloads.DuckProperties;
import autotests.payloads.DuckSoundResponse;
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
@Feature("Кряканье уточки")
@Story("Эндпоинт /api/duck/action/quack")
public class DuckActionQuack extends DuckActionsClient {

    // TODO: SHIFT-AQA-02
    @Test(description = "Проверка способности крякать при четном ID")
    @CitrusTest
    public void oddQuack(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        final int REPETITION_COUNT = 3;
        final int SOUND_COUNT = 4;
        //final String SOUND = "quack";
        final String SOUND = "moo";

        DuckProperties duckQuackEven = new DuckProperties()
                .color("Black")
                .height(10.0)
                .material("iron")
                .sound(SOUND)
                .wingsState("ACTIVE");

        createDuckEnsuringIdParity(runner, context, duckQuackEven, true);
        duckQuack(runner, "${duckId}", REPETITION_COUNT, SOUND_COUNT);

        DuckSoundResponse soundMessage = new DuckSoundResponse().sound(SOUND);
        validateResponseSound(runner, soundMessage, REPETITION_COUNT, SOUND_COUNT);
    }

    @Test(description = "Проверка способности крякать при нечетном ID")
    @CitrusTest
    public void quackDuckWithOddId(@Optional @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context) {
        final int REPETITION_COUNT = 3;
        final int SOUND_COUNT = 4;
        final String SOUND = "quack";

        DuckProperties duckQuackOdd = new DuckProperties()
                .color("Blue")
                .height(10.10)
                .material("Wood")
                .sound(SOUND)
                .wingsState("ACTIVE");

        createDuckEnsuringIdParity(runner, context, duckQuackOdd, false);
        duckQuack(runner, "${duckId}", REPETITION_COUNT, SOUND_COUNT);

        DuckSoundResponse soundMessage = new DuckSoundResponse().sound(SOUND);
        validateResponseSound(runner, soundMessage, REPETITION_COUNT, SOUND_COUNT);
    }
}
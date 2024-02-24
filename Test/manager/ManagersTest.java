package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void shouldReturnInitializedAndReadyToWorkTaskManager() {

        assertNotNull(Managers.getDefault(), "Manager.TaskManager не создан");
    }

    @Test
    void shouldReturnInitializedAndReadyToWorkHistoryManager() {
        assertNotNull(Managers.getDefaultHistory(), "Manager.HistoryManager не создан");
    }
}
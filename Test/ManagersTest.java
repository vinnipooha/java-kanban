import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void shouldReturnInitializedAndReadyToWorkTaskManager() {

        assertNotNull(Managers.getDefault(), "TaskManager не создан");
    }

        @Test
        public void shouldReturnInitializedAndReadyToWorkHistoryManager() {
        assertNotNull(Managers.getDefaultHistory(), "HistoryManager не создан");
    }
}
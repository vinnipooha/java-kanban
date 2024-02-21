package manager;

import java.nio.file.Paths;

public final class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(getDefaultHistory(), Paths.get("sourses/tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
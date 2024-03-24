package manager;

import java.nio.file.Paths;

public final class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(Paths.get("sourses/tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultInMemoryManager() { return  new InMemoryTaskManager(getDefaultHistory()); }

}
import java.util.ArrayList;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<SubTask> getAllSubTasks();

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    SubTask getSubTaskById(int subTaskId);

    ArrayList<SubTask> getSubTasksByEpic(int epicId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubTaskById(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    ArrayList<Task> getHistory();
}

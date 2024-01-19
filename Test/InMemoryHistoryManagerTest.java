import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    public void shouldSaveThePreviousVersionOfTheObject() {
        Task task = taskManager.createTask(new Task("Model.Task", "Model.Task.descr"));
        Epic epic = taskManager.createEpic(new Epic("Model.Epic", "Model.Epic.descr"));
        SubTask subTask = taskManager.createSubTask(new SubTask("ST", "STdescr", 2));

        taskManager.getTaskById(1);
        assertEquals(1, taskManager.getHistory().size(), "Добавление просмотров в историю не работает");
        taskManager.getEpicById(2);
        assertEquals(2, taskManager.getHistory().size());
        taskManager.getSubTaskById(3);

        assertEquals(3, taskManager.getHistory().size());
        Task taskForUpdate = new Task(1, "Model.Task test update", "Model.Task descr update");
        taskManager.updateTask(taskForUpdate);
        taskManager.getTaskById(1);
        assertEquals(4, taskManager.getHistory().size(),
                "Обновление истории после обновления задачи работает некорректно");
        List<Task> history = taskManager.getHistory();
        assertEquals(task, history.get(0),
                "Первая добавленная задача должна быть равна первому объекту списка истории");
        assertNotEquals(history.get(0), history.get(3),
                "Сохранение предыдущей версии обновленной задачи на работает");
        assertEquals(taskForUpdate, history.get(3),
                "Последий элемент списка должен быть равен последней просмотренной задаче");
    }

}
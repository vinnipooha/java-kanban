import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    public void shouldSaveThePreviousVersionOfTheObject() {
        Task task = taskManager.createTask(new Task("Task", "Task.descr"));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Epic.descr"));
        SubTask subTask = taskManager.createSubTask(new SubTask("ST", "STdescr", 2));

        taskManager.getTaskById(1);
        assertEquals(1, taskManager.getHistory().size(), "Добавление просмотров в историю не работает");
        taskManager.getEpicById(2);
        assertEquals(2, taskManager.getHistory().size());
        taskManager.getSubTaskById(3);

        assertEquals(3, taskManager.getHistory().size());
        Task taskForUpdate = new Task(1, "Task test update", "Task descr update");
        taskManager.updateTask(taskForUpdate);
        taskManager.getTaskById(1);
        assertEquals(4, taskManager.getHistory().size(),
                "Обновление истории после обновления задачи работает некорректно");
        ArrayList<Task> history = taskManager.getHistory();
        assertEquals(task, history.get(0),
                "Первая добавленная задача должна быть равна первому объекту списка истории");
        assertNotEquals(history.get(0), history.get(3),
                "Сохранение предыдущей версии обновленной задачи на работает");
        assertEquals(taskForUpdate, history.get(3),
                "Последий элемент списка должен быть равен последней просмотренной задаче");
    }

}
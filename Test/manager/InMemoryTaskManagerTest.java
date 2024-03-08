package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void shouldNotNotAddTaskIfItIntersects() {
        Task task1 = taskManager.createTask(new Task("Task1", "T_descr1", now, duration));
        Task task2 = taskManager.createTask(new Task("Task2", "T_descr2", now.plusMinutes(10), duration));
        Task task3 = taskManager.createTask(new Task("Task3", "T_descr3", now.plusMinutes(5), duration));
        Task task4 = taskManager.createTask(new Task("Task4", "T_descr4", now, duration));
        Task task5 = taskManager.createTask(new Task("Task5", "T_descr5", now.minusMinutes(5), duration.plusMinutes(6)));

        assertEquals(2, taskManager.tasks.size(), "Проверка на пересечение задач не работает");
        assertFalse(taskManager.tasks.containsKey(3) && taskManager.tasks.containsKey(4) &&
                taskManager.tasks.containsKey(5), "Отбраковка пересекающихся задач не работает");
    }

}
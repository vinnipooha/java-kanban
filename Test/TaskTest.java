import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void AfterCreateTaskIdIsGreaterThen0AndNextAdd1(){
        Task task2 = taskManager.createTask(new Task("Task 1", "descr1"));
        Task task3 = taskManager.createTask(new Task("Task 2", "descr2"));
        assertTrue(task2.getId() > 0, "Счетчик id не работает");
        assertEquals(1, (task3.getId() - task2.getId()), "Счетчик id некорректно считает значения");
    }
    @Test
    void TasksAreEqualIfTheirIdIsEqual() {
        Task task = taskManager.createTask (new Task("Task 1", "descr1"));
        int taskId = task.getId();
        Task task1 = new Task(taskId, "Task 1", "descr1");
        assertEquals(task, task1, "Таски с одинаковым id должны быть равны");
    }

    @Test
    void TaskHasNewStatusAfterCreate() {
        Task result = taskManager.createTask(new Task("Task 2", "descr2"));
        assertEquals(Status.NEW, result.getStatus(), "Некорректная генерация статуса");
    }

    }
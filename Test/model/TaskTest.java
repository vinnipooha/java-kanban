package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    TaskManager taskManager;
    protected LocalDateTime now = LocalDateTime.now();
    protected Duration duration = Duration.ofMinutes(9);

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }
    @Test
    void AfterCreateTaskIdIsGreaterThen0AndNextAdd1() {
        Task task1 = taskManager.createTask(new Task("Model.Task 1", "descr1", now, duration));
        Task task2 = taskManager.createTask(new Task("Model.Task 2", "descr2", now.plusMinutes(10), duration));
        assertTrue(task1.getId() > 0, "Счетчик id не работает");
        assertEquals(1, (task2.getId() - task1.getId()), "Счетчик id некорректно считает значения");
    }

    @Test
    void TasksAreEqualIfTheirIdIsEqual() {
        Task task = taskManager.createTask(new Task("Model.Task 1", "descr1", now, duration));
        int taskId = task.getId();
        Task task1 = new Task(taskId, "Model.Task 1", "descr1", Status.NEW, now, duration);
        assertEquals(task, task1, "Таски с одинаковым id должны быть равны");
    }

    @Test
    void TaskHasNewStatusAfterCreate() {
        Task result = taskManager.createTask(new Task("Model.Task 2", "descr2", now, duration));
        assertEquals(Status.NEW, result.getStatus(), "Некорректная генерация статуса");
    }

}
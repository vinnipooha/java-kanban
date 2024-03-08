package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTaskTest {
    TaskManager taskManager = Managers.getDefault();
    protected LocalDateTime now = LocalDateTime.now();
    protected Duration duration = Duration.ofMinutes(9);
    @Test
    void SubTasksAreEqualIfTheirIdIsEqual() {
        Epic epic = taskManager.createEpic(new Epic("Epic1", "descr1"));
        int epicId = epic.getId();
        SubTask subTask1 = taskManager.createSubTask(new SubTask("ST 1", "descr1", epicId, now, duration));
        SubTask subTaskExpected = new SubTask(2, "ST 1", "descr1", Status.NEW, epicId, now, duration);
        assertEquals(subTaskExpected, subTask1, "Сабтаски с одинаковым id должны быть равны");
    }


}
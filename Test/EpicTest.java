import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void EpicsAreEqualIfTheirIdIsEqual() {
    Epic epic = new Epic("Epic1", "description1");
    Epic result = taskManager.createEpic(epic);
    int resultId = result.getId();
    Epic epic1 = new Epic(resultId, "Epic1", "description1");
    assertTrue(resultId > 0, "Счетчик id не работает");
    assertEquals(result, epic1, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    public void EpicHasNewStatusAfterCreate() {
        Epic epic = new Epic("Epic2", "description2");
        Epic result = taskManager.createEpic(epic);
        assertEquals(Status.NEW, result.getStatus(), "Рассчет статуса эпика при отсутствии сабтасок неверен");
    }
    @Test
    public void EpicHasNewStatusWhenAllSubTasksAreNew() {
        Epic epic = taskManager.createEpic(new Epic("Epic3", "descr3"));
        int epicId = epic.getId();
        SubTask result1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId));
        SubTask result2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId));
        assertEquals(Status.NEW, result1.getStatus());
        assertEquals(Status.NEW, result2.getStatus());
        assertEquals(Status.NEW, epic.getStatus(), "Рассчет статуса эпика при новых сабтасках некорректна");
    }

    @Test
    public void EpicHasInProgressStatusWhenSubTasksAreNewAndDone() {
        Epic epic = taskManager.createEpic(new Epic("Epic4", "descr4"));
        int epicId = epic.getId();
        SubTask result1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId));
        SubTask result2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId));
        int result1Id = result1.getId();
        taskManager.updateSubTask(new SubTask(result1Id, "ST1", "descr1", Status.DONE, epicId));
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Рассчет статуса эпика при NEW+DONE некорректен");
        }

    @Test
    public void EpicHasInProgressStatusWhenSubTasksAreInProgress() {
        Epic epic = taskManager.createEpic(new Epic("Epic5", "descr5"));
        int epicId = epic.getId();
        SubTask result1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId));
        SubTask result2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId));
        int result1Id = result1.getId();
        int result2Id = result2.getId();
        taskManager.updateSubTask(new SubTask(result1Id, "ST1", "descr1", Status.IN_PROGRESS, epicId));
        taskManager.updateSubTask(new SubTask(result2Id, "ST2", "descr2", Status.IN_PROGRESS, epicId));
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Рассчет статуса эпика при IN_PROGRESS некорректен");
    }
    @Test
    public void EpicHasDoneStatusWhenAllSubTasksAreDone() {
        Epic epic = taskManager.createEpic(new Epic("Epic6", "descr6"));
        int epicId = epic.getId();
        SubTask result1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId));
        SubTask result2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId));
        int result1Id = result1.getId();
        int result2Id = result2.getId();
        taskManager.updateSubTask(new SubTask(result1Id, "ST1", "descr1", Status.DONE, epicId));
        taskManager.updateSubTask(new SubTask(result2Id, "ST2", "descr2", Status.DONE, epicId));
        assertEquals(Status.DONE, epic.getStatus(), "Рассчет статуса эпика при выполненных сабтасках неверен");
    }
}
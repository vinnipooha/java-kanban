import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTaskTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void SubTasksAreEqualIfTheirIdIsEqual() {
        Epic epic = taskManager.createEpic(new Epic("Epic1", "descr1"));
        int epicId = epic.getId();
        SubTask subTask1 = taskManager.createSubTask(new SubTask("ST 1", "descr1", epicId));
        SubTask subTaskExpected = taskManager.createSubTask(new SubTask(subTask1.getId(),"ST 1",
                "descr1", Status.NEW, epicId));
        assertEquals(subTaskExpected, subTask1, "Сабтаски с одинаковым id должны быть равны");
    }


}
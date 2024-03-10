package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected LocalDateTime now = LocalDateTime.now();
    protected Duration duration = Duration.ofMinutes(9);

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
    void EpicHasNewStatusAfterCreate() {
        Epic epic = new Epic("Epic2", "description2");
        Epic result = taskManager.createEpic(epic);
        assertEquals(Status.NEW, result.getStatus(), "Рассчет статуса эпика при отсутствии сабтасок неверен");
    }

    @Test
    void EpicHasNewStatusWhenAllSubTasksAreNew() {
        Epic epic = taskManager.createEpic(new Epic("Epic3", "descr3"));
        int epicId = epic.getId();
        SubTask result1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId, now, duration));
        SubTask result2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId, now.plusMinutes(10), duration));
        assertEquals(Status.NEW, result1.getStatus());
        assertEquals(Status.NEW, result2.getStatus());
        assertEquals(Status.NEW, epic.getStatus(), "Рассчет статуса эпика при новых сабтасках некорректна");
    }

    @Test
    void EpicHasInProgressStatusWhenSubTasksAreNewAndDone() {
        Epic epic = taskManager.createEpic(new Epic("Epic4", "descr4"));
        int epicId = epic.getId();
        SubTask result1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId, now, duration));
        SubTask result2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId, now.plusMinutes(10), duration));
        taskManager.updateSubTask(new SubTask(2, "ST1", "descr1", Status.DONE, epicId, now, duration));
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Рассчет статуса эпика при NEW+DONE некорректен");
    }

    @Test
    void EpicHasInProgressStatusWhenSubTasksAreInProgress() {
        Epic epic = taskManager.createEpic(new Epic("Epic5", "descr5"));
        int epicId = epic.getId();
        SubTask result1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId, now, duration));
        SubTask result2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId, now.plusMinutes(10), duration));
        int result1Id = result1.getId();
        int result2Id = result2.getId();
        taskManager.updateSubTask(new SubTask(result1Id, "ST1", "descr1", Status.IN_PROGRESS, epicId, now, duration));
        taskManager.updateSubTask(new SubTask(result2Id, "ST2", "descr2", Status.IN_PROGRESS, epicId, now.plusMinutes(10), duration));
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Рассчет статуса эпика при IN_PROGRESS некорректен");
    }

    @Test
    void EpicHasDoneStatusWhenAllSubTasksAreDone() {
        Epic epic = taskManager.createEpic(new Epic("Epic6", "descr6"));
        int epicId = epic.getId();
        SubTask result1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId, now, duration));
        SubTask result2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId, now.plusMinutes(10), duration));
        int result1Id = result1.getId();
        int result2Id = result2.getId();
        taskManager.updateSubTask(new SubTask(result1Id, "ST1", "descr1", Status.DONE, epicId, now, duration));
        taskManager.updateSubTask(new SubTask(result2Id, "ST2", "descr2", Status.DONE, epicId, now.plusMinutes(10), duration));
        assertEquals(Status.DONE, epic.getStatus(), "Рассчет статуса эпика при выполненных сабтасках неверен");
    }

    @Test
    void SubTasksAreEqualIfTheirIdIsEqual() {
        Epic epic = taskManager.createEpic(new Epic("Epic1", "descr1"));
        int epicId = epic.getId();
        SubTask subTask1 = taskManager.createSubTask(new SubTask("ST 1", "descr1", epicId, now, duration));
        SubTask subTaskExpected = new SubTask(2, "ST 1", "descr1", Status.NEW, epicId, now, duration);
        assertEquals(subTaskExpected, subTask1, "Сабтаски с одинаковым id должны быть равны");
    }


    @Test
    void canNotAddEpicAsItsSubtask() {
        Epic epic = taskManager.createEpic(new Epic("Epic1", "descr1"));
        SubTask subTask = taskManager.createSubTask(new SubTask("ST1", "STdescr", epic.getId(), now, duration));
        int epicId = epic.getId();
        SubTask epicTest = new SubTask(epicId, "Epic1", "descr1", Status.NEW, epicId, now, duration);
        taskManager.updateSubTask(epicTest);
        assertNull(taskManager.getSubTaskById(epicId), "Model.Epic нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    void canNotAddSubTaskAsItsEpic() {
        Epic epic = taskManager.createEpic(new Epic("Model.Epic", "descr"));
        SubTask subTask = taskManager.createSubTask(new SubTask("ST1", "STdescr", epic.getId(), now, duration));
        int subTaskId = subTask.getId();
        SubTask subTaskTest = new SubTask(subTaskId, "ST", "STdescr", Status.NEW, subTaskId, now, duration);
        assertNull(taskManager.createSubTask(subTaskTest), "Subtask нельзя сделать своим же эпиком");

    }

    @Test
    void shouldCreateNewTaskAndSearchById() {
        Task task = taskManager.createTask(new Task("Model.Task", "Model.Task.descr", now, duration));
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldCreateNewEpicAndSearchById() {
        Epic epic = taskManager.createEpic(new Epic("Model.Epic", "Model.Epic.descr"));
        final int epicId = epic.getId();

        final Task savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final ArrayList<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldCreateNewSubTaskAndSearchById() {
        Epic epic = taskManager.createEpic(new Epic("Model.Epic", "Model.Epic.descr"));
        SubTask subTask = taskManager.createSubTask(new SubTask("ST", "STdescr", epic.getId(), now, duration));
        final int subTaskId = subTask.getId();

        List<Integer> subTasksByEpic = epic.getSubTasks();
        final int subTaskByEpicId = subTasksByEpic.getFirst();

        assertEquals(subTaskId, subTaskByEpicId, "Неверно добавляются id сабтасок в эпики.");

        final Task savedSubTask = taskManager.getSubTaskById(subTaskId);

        assertNotNull(savedSubTask, "Сабтаска не найдена.");
        assertEquals(subTask, savedSubTask, "Сабтаски не совпадают.");

        final ArrayList<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void tasksWhithTheSpecifiedAndGeneratedIdDoNotConflict() {
        Task task1 = taskManager.createTask(new Task("Task1", "Model.Task.descr1", now, duration));
        assertEquals(1, task1.getId(), "У первой задачи id должен быть равен 1");
        Task task2 = taskManager.createTask(new Task(2, "Task2", "Model.Task.descr2", Status.NEW, now.plusMinutes(10), duration));
        assertEquals(2, task2.getId(), "У второй задачи id должен быть равен 2");
        assertEquals(2, taskManager.getAllTasks().size(), "Задачи с заданным id менеджер не может обработать");
        Task task3 = new Task(1, "Task3", "Model.Task.descr3", Status.NEW, now.plusMinutes(20), duration);
        Task taskSaved = taskManager.createTask(task3);
        assertEquals(3, taskSaved.getId(), "Задача с заданным id затирает созданную ранее задачу с таким же id");
        assertEquals(task1, taskManager.getAllTasks().getFirst(), "Первая задача (со сгенерированным id) добавлена первой в список");
        assertEquals(task2, taskManager.getAllTasks().get(1), "Вторая задача (с заданным id) добавлена второй в список");
    }

    @Test
    void epicShouldNotContainIrrelevantSubtasks() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "descr"));
        int epicId = epic.getId();
        SubTask subTask1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId, now, duration));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId, now.plusMinutes(10), duration));

        int result1Id = subTask1.getId();
        taskManager.deleteSubTaskById(result1Id);

        assertEquals(1, taskManager.getSubTasksByEpic(epicId).size(), "Удаление подзадач из эпика не работает");
        assertFalse(taskManager.getSubTasksByEpic(epicId).contains(subTask1), "Внутри эпика неактуальный id подзадачи");
    }

    @Test
    void subTaskForDeleteShouldNotContainIrrelevantId() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "descr"));
        int epicId = epic.getId();
        SubTask subTask1 = taskManager.createSubTask(new SubTask("ST1", "descr1", epicId, now, duration));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("ST2", "descr2", epicId, now.plusMinutes(10), duration));
        int subTask2Id = subTask2.getId();
        subTask2.setEpicId(3);

        taskManager.deleteSubTaskById(subTask2Id);
        assertEquals(2, taskManager.getAllSubTasks().size(), "Удаление подзадачи с неактуальным эпиком");
    }


}
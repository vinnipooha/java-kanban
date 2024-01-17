import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    protected TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
     taskManager = Managers.getDefault();
    }

    @Test
    public void canNotAddEpicAsItsSubtask() {
        Epic epic = taskManager.createEpic(new Epic("Epic1", "descr1"));
        SubTask subTask = taskManager.createSubTask(new SubTask("ST1", "STdescr", epic.getId()));
        int epicId = epic.getId();
        SubTask epicTest =  new SubTask(epicId,"Epic1", "descr1", Status.NEW, epicId);
        taskManager.updateSubTask(epicTest);
        assertNull(taskManager.getSubTaskById(epicId), "Epic нельзя добавить в самого себя в виде подзадачи");
    }
    @Test
    public void canNotAddSubTaskAsItsEpic() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "descr"));
        SubTask subTask = taskManager.createSubTask(new SubTask("ST1", "STdescr", epic.getId()));
        int subTaskId = subTask.getId();
        SubTask subTaskTest = new SubTask(subTaskId, "ST", "STdescr", Status.NEW , subTaskId);
        assertNull(taskManager.createSubTask(subTaskTest), "Subtask нельзя сделать своим же эпиком");

    }

    @Test
    public void shouldCreateNewTaskAndSearchById() {
        Task task = taskManager.createTask(new Task("Task", "Task.descr"));
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
    public void shouldCreateNewEpicAndSearchById() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Epic.descr"));
        final int epicId = epic.getId();

        final Task savedEpic= taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final ArrayList<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void shouldCreateNewSubTaskAndSearchById() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Epic.descr"));
        SubTask subTask = taskManager.createSubTask(new SubTask("ST", "STdescr", epic.getId()));
        final int subTaskId = subTask.getId();

        ArrayList<Integer> subTasksByEpic = epic.getSubTasks();
        final int subTaskByEpicId = subTasksByEpic.getFirst();

        assertEquals(subTaskId, subTaskByEpicId, "Неверно добавляются id сабтасок в эпики.");

        final Task savedSubTask= taskManager.getSubTaskById(subTaskId);

        assertNotNull(savedSubTask, "Сабтаска не найдена.");
        assertEquals(subTask, savedSubTask, "Сабтаски не совпадают.");

        final ArrayList<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void tasksWhithTheSpecifiedAndGeneratedIdDoNotConflict() {
        Task task1 = taskManager.createTask(new Task("Task1", "Task.descr1"));
        assertEquals(1, task1.getId(), "У первой задачи id должен быть равен 1");
        Task task2 = taskManager.createTask(new Task(2, "Task2", "Task.descr2", Status.NEW));
        assertEquals(2, task2.getId(), "У второй задачи id должен быть равен 2");
        assertEquals(2, taskManager.getAllTasks().size(), "Задачи с заданным id менеджер не может обработать");
        Task task3 = new Task(1, "Task3", "Task.descr3", Status.NEW);
        Task taskSaved = taskManager.createTask(task3);
        assertEquals(3, taskSaved.getId(), "Задача с заданным id затирает созданную ранее задачу с таким же id");
        assertEquals(task1, taskManager.getAllTasks().getFirst(), "Первая задача (со сгенерированным id) добавлена первой в список");
        assertEquals(task2, taskManager.getAllTasks().get(1), "Вторая задача (с заданным id) добавлена второй в список");
    }
}
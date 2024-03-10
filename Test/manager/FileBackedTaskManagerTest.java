package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    protected Path tempPath;

    @BeforeEach
    void beforeEach() throws IOException {
        try {
            File tempFile = File.createTempFile("test-", ".tmp");
            tempPath = tempFile.toPath();
            tempFile.deleteOnExit();
            taskManager = new FileBackedTaskManager(tempPath);
        } catch (IOException e) {
            throw new IOException("Произошла ошибка создания файла");
        }
    }

    @Test
    void shouldSaveAndLoadIsEmptyFile() throws IOException {

        assertTrue(Files.exists(tempPath), "Файл для записи данных не был создан");

        try {
            String contentsOfTheFile = Files.readString(tempPath);
            assertTrue(contentsOfTheFile.isEmpty(), "После создания менеджера файл должен быть пустым");
            FileBackedTaskManager fileManagerTest = FileBackedTaskManager.loadFromFile(tempPath);
            assertNotNull(fileManagerTest, "Загрузка из пустого файла не сработала");
            assertTrue(fileManagerTest.getAllTasks().isEmpty(), "После загрузки из пустого файла список задач должен быть пустым");
            assertTrue(fileManagerTest.getAllEpics().isEmpty(), "После загрузки из пустого файла список эпиков должен быть пустым");
            assertTrue(fileManagerTest.getAllSubTasks().isEmpty(), "После загрузки из пустого файла список подзадач должен быть пустым");
            assertTrue(fileManagerTest.getHistory().isEmpty(), "После загрузки из пустого файла история должна отсутствовать");
        } catch (IOException e) {
            throw new IOException("Произошла ошибка чтения файла");
        }
    }

    @Test
    void shouldSaveSeveralTasksInFile() throws FileNotFoundException {
        Task task1 = taskManager.createTask(new Task(".Task1", "Task1_descr", now, duration));
        Epic epic1 = taskManager.createEpic(new Epic("Epic1", "Epic1_descr1"));
        Epic epic2 = taskManager.createEpic(new Epic("Epic2", "Epic1_descr2"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("ST1", "ST1_descr", 2, now.plusMinutes(10), duration));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("ST2", "ST2_descr", 2, now.plusMinutes(20), duration));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("ST3", "ST3_descr", 3, now.plusMinutes(30), duration));

        assertTrue(Files.exists(tempPath), "Файл для хранения данных не был создан");

        try (BufferedReader reader = new BufferedReader(new FileReader(tempPath.toString()))) {
            String line = reader.readLine();
            assertEquals("id,type,name,status,description,startTime,duration,epic", line, "Первой строкой должен быть заголовок");
            for (int i = 1; i <= 6; i++) {
                String testLine = reader.readLine();
                if (i == 1) assertEquals(task1.toString(), testLine, "Второй строкой должна быть запись task1");
                if (i == 3) assertEquals(epic2.toString(), testLine, "Четвертой строкой должна быть запись subTask1");
                if (i == 6) assertEquals(subTask3.toString(), testLine, "");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения из файла", e);
        }
    }

    @Test
    void shouldLoadFromFileSeveralTasksAndHistory() {
        Task task1 = taskManager.createTask(new Task("Task1", "Task1_descr", now, duration));
        Epic epic1 = taskManager.createEpic(new Epic("Epic1", "Epic1_descr"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("ST1", "ST1_descr", 2, now.plusMinutes(10), duration));
        Epic epic2 = taskManager.createEpic(new Epic("Epic2", "Epic2_descr"));

        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubTaskById(3);

        FileBackedTaskManager fileManagerToTest = FileBackedTaskManager.loadFromFile(tempPath);
        assertNotNull(fileManagerToTest, "Ошибка при создании менеджера");
        assertEquals(1, fileManagerToTest.getAllTasks().size(), "Ошибка загрузки тасок");
        assertEquals(2, fileManagerToTest.getAllEpics().size(), "Ошибка загрузки эпиков");
        assertEquals(1, fileManagerToTest.getAllSubTasks().size(), "Ошибка загрузки сабтасок");
        assertEquals(3, fileManagerToTest.getHistory().size(), "Ошибка загрузки истории");
        assertEquals(3, fileManagerToTest.getHistory().getFirst().getId(),
                "Нарушен порядок выведения просмотренных задач в истории");
    }

    @Test
    void testExceptionByLoadFromMissingFile() {
        assertThrows(RuntimeException.class, () -> {
            FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(Paths.get("sourses"));
        },"Загрузка из несуществующего файла должна привести к исключению");
    }

    @Test
    void testExceptionBySaveToMissingFile() {
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Paths.get("sourses"));
                    fileBackedTaskManager.createTask(new Task("Task", "T_descr", now, duration));
        },"Загрузка в несуществующий файл должна привести к исключению");
    }

}
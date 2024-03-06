package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static manager.CSVFormat.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    protected Path path;
    static final String HEADER = "id,type,name,status,description,startTime,duration,epic\n";

    public FileBackedTaskManager(Path path) {
        super(Managers.getDefaultHistory());
        this.path = path;

    }

    public static void main(String[] args) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(Paths.get("sourses/saveTasksTest.csv"));
        fileManager.createTask(new Task("Task1", "T_descr"));
        fileManager.createEpic(new Epic("Epic1", "E_descr1"));
        fileManager.createSubTask(new SubTask("ST1", "ST_descr1", 2, LocalDateTime.now(), Duration.ofMinutes(60)));
        fileManager.createSubTask(new SubTask("ST2", "ST_descr2", 2, LocalDateTime.now().plusHours(2), Duration.ofMinutes(15)));

        fileManager.getTaskById(1);
        fileManager.getEpicById(2);
        fileManager.getSubTaskById(3);

        System.out.println(fileManager.getAllTasks());
        System.out.println(fileManager.getAllEpics());
        System.out.println(fileManager.getAllSubTasks());
        System.out.println("История:");
        System.out.println(fileManager.getHistory());
        System.out.println("Приоритеты:");
        //System.out.println(fileManager.getPrioritizedTasks());

        System.out.println("\n" + "new" + "\n");

        FileBackedTaskManager fileManagerToLoad = loadFromFile(Paths.get("sourses/saveTasksTest.csv"));

        System.out.println(fileManagerToLoad.getAllTasks());
        System.out.println(fileManagerToLoad.getAllEpics());
        System.out.println(fileManagerToLoad.getAllSubTasks());
        System.out.println("История:");
        System.out.println(fileManagerToLoad.getHistory());
        System.out.println("Приоритеты:");
        //System.out.println(fileManagerToLoad.getPrioritizedTasks());
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(path.toString(), StandardCharsets.UTF_8)) {
            fileWriter.write(HEADER);
            for (Integer key : tasks.keySet()) {
                fileWriter.write(tasks.get(key).toString() + "\n");
            }
            for (Integer key : epics.keySet()) {
                fileWriter.write(epics.get(key).toString() + "\n");
            }
            for (Integer key : subTasks.keySet()) {
                fileWriter.write(subTasks.get(key).toString() + "\n");
            }
            fileWriter.write("\n");
            if (!historyManager.getHistory().isEmpty()) {
                fileWriter.write(historyToString(historyManager));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка записи в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager fileBakedManager = new FileBackedTaskManager(path);
        fileBakedManager.load();
        return fileBakedManager;
    }

    private void load() {
        int maxId = 0; // Для актуализации значений счетчика
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toString()))) {
            bufferedReader.readLine();  //прочитали заголовок
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }

                Task task = taskFromString(line);
                int id = task.getId();

                switch (task.getType()) {
                    case TASK:
                        tasks.put(id, task);
                        if (task.getStartTime() != null) prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        epics.put(id, (Epic) task);
                        break;
                    case SUBTASK:
                        subTasks.put(id, (SubTask) task);
                        Epic epic = epics.get(subTasks.get(id).getEpicId());
                        epic.addSubTask(id);
                        if (task.getStartTime() != null) prioritizedTasks.add(task);
                        break;
                }
                if (maxId < id) {
                    maxId = id;
                }
            }
            String line = bufferedReader.readLine(); //прочитали строку истории
            if (line != null && !line.isEmpty()) {
                List<Integer> historyId = historyFromString(line);
                for (Integer valueId : historyId) {
                    if (tasks.containsKey(valueId)) {
                        historyManager.add(tasks.get(valueId));
                    } else if (epics.containsKey(valueId)) {
                        historyManager.add(epics.get(valueId));
                    } else if (subTasks.containsKey(valueId)) {
                        historyManager.add(subTasks.get(valueId));
                    }
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Ошибка: файл не найден", e);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения из файла", e);
        }
        setId(maxId);
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubTask = super.createSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = super.getSubTaskById(subTaskId);
        save();
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }
}

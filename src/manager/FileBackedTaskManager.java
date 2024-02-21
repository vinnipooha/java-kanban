package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    protected Path path;
    static final String HEADER = "id,type,name,status,description,epic\n";

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    public void save() {
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
        FileBackedTaskManager fileBakedManager = new FileBackedTaskManager(Managers.getDefaultHistory(), path);
        fileBakedManager.load();
        return fileBakedManager;
    }

    public void load() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toString()))) {
            bufferedReader.readLine();  //прочитали заголовок
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.isEmpty()) {
                    break;
                }
                Task task = taskFromString(line);
                int id = task.getId();

                switch (task.getType()) {
                    case TASK:
                        tasks.put(id, task);
                        break;
                    case EPIC:
                        epics.put(id, (Epic) task);
                        break;
                    case SUBTASK:
                        subTasks.put(id, (SubTask) task);
                        Epic epic = epics.get(subTasks.get(id).getEpicId());
                        epic.addSubTask(id);
                        break;
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
    }

    public Task taskFromString(String value) throws RuntimeException {
        String[] columns = value.split(",");
        int id = Integer.parseInt(columns[0]);

        Status status = null;
        Status[] statuses = Status.values();
        for (int i = 0; i < statuses.length && status == null; i++) {
            if (statuses[i].getStatus().equals(columns[3])) {
                status = statuses[i];
            }
        }
        if (status == null) {
            throw new RuntimeException("Статус не найден: " + columns[3]);
        }
        Type type = Type.valueOf(columns[1]);

        Task task;

        switch (type) {
            case TASK:
                task = new Task(id, columns[2], columns[4], status);
                break;

            case EPIC:
                task = new Epic(id, columns[2], columns[4]);
                task.setStatus(status);
                break;

            case SUBTASK:
                task = new SubTask(id, columns[2], columns[4], status, Integer.parseInt(columns[5]));
                break;

            default:
                throw new ManagerSaveException(("Неизвестный тип объекта " + type));
        }

        return task;
    }

    static String historyToString(HistoryManager historyManager) {
        List<Task> historyList = historyManager.getHistory();
        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (Task task : historyList) {
            if (counter < historyList.size() - 1) {
                builder.append(task.getId());
                builder.append(",");
            } else {
                builder.append(task.getId());
            }
            counter++;
        }
        return builder.toString();
    }

    static List<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        List<Integer> list = new ArrayList<>();
        for (int i = split.length - 1; i >= 0; i--) {
            list.add(Integer.valueOf(split[i]));
        }
        return list;
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

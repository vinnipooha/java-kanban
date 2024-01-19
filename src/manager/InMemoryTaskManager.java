package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;

    private int id = 0;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    private int generateId() {
        return ++id;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        epic.setStatus(calculateStatus(epic));
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            return null;
        }
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epicForUpdate = epics.get(epicId);
        epicForUpdate.addSubTask(subTask.getId());
        epicForUpdate.setStatus(calculateStatus(epicForUpdate));
        return subTask;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> idSubTasksByEpic = epic.getSubTasks();
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        for (int idSubTask : idSubTasksByEpic) {
            subTasksByEpic.add(subTasks.get(idSubTask));
        }
        return subTasksByEpic;
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        if (!tasks.containsKey(taskId)) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }
        Epic epicForUpdate = epics.get(epic.getId());
        epicForUpdate.setName(epic.getName());
        epicForUpdate.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            return;
        }
        int epicId = subTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            return;
        }
        ArrayList<Integer> subTasksOfEpic = (epics.get(epicId)).getSubTasks();
        if (!subTasksOfEpic.contains(subTask.getId())) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        Epic epicForUpdate = epics.get(epicId);
        epicForUpdate.setStatus(calculateStatus(epicForUpdate));
    }

    private Status calculateStatus(Epic epic) {
        ArrayList<Integer> subTaskList = epic.getSubTasks();
        if (subTaskList.isEmpty()) {
            return Status.NEW;
        }
        int newStatus = 0;
        int doneStatus = 0;
        for (Integer subTaskId : subTaskList) {
            if (subTasks.get(subTaskId).getStatus().equals(Status.NEW)) {
                newStatus++;
            }
            if (subTasks.get(subTaskId).getStatus().equals(Status.DONE)) {
                doneStatus++;
            }
        }
        if (newStatus == subTaskList.size()) {
            return Status.NEW;
        }
        if (doneStatus == subTaskList.size()) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        Epic epicForDelete = epics.get(id);
        for (Integer subTaskId : epicForDelete.getSubTasks()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            return;
        }
        SubTask subTask = subTasks.get(id);
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        subTasks.remove(id);
        epic.deleteSubTask(id);
        epic.setStatus(calculateStatus(epic));
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubTasks();
            epic.setStatus(calculateStatus(epic));
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}



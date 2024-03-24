package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.naturalOrder;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager;
    protected final Set<Task> prioritizedTasks;

    private int id = 0;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsFirst(naturalOrder())).thenComparing(Task::getId));
    }

    private boolean isIntersect(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return false;
        }
        LocalDateTime start = task.getStartTime();
        LocalDateTime finish = task.getEndTime();

        if (start == null) {
            return false;
        }

        for (Task prioritizedTask : prioritizedTasks) {

            LocalDateTime localStart = prioritizedTask.getStartTime();
            LocalDateTime localFinish = prioritizedTask.getEndTime();

            if (start.isEqual(localStart) || start.isEqual(localFinish)
                    || finish.isEqual(localStart) || finish.isEqual(localFinish)) {
                return true;
            }
            if (start.isAfter(localStart) && start.isBefore(localFinish)
                    || finish.isAfter(localStart) && finish.isBefore(localFinish)) {
                return true;
            }
            if (start.isBefore(localStart) && finish.isAfter(localFinish)) {
                return true;
            }
        }
        return false;
    }

    private int generateId() {
        return ++id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Task createTask(Task task) {
        if (isIntersect(task)) { // Задача пересекается по времени с имеющимися задачами
            return null;
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
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
        if (!epics.containsKey(epicId)) return null;

        if (isIntersect(subTask)) return null; // Подзадача пересекается по времени с другими задачами

        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epicForUpdate = epics.get(epicId);
        epicForUpdate.addSubTask(subTask.getId());
        epicForUpdate.setStatus(calculateStatus(epicForUpdate));
        calculateEpicDateTime(epicForUpdate);

        if (subTask.getStartTime() != null) prioritizedTasks.add(subTask);
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
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return null;
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask == null) return null;
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> idSubTasksByEpic = epic.getSubTasks();
        ArrayList<SubTask> subTasksByEpic = idSubTasksByEpic.stream()
                .mapToInt(idSubTask -> idSubTask)
                .mapToObj(subTasks::get)
                .collect(Collectors.toCollection(ArrayList::new));
        return subTasksByEpic;
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        if (!tasks.containsKey(taskId)) return;
        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask);
        if (isIntersect(task)) {
            prioritizedTasks.add(oldTask);
            return;
        } // Задача пересекается по времени с имеющимися задачами

        if (task.getStartTime() != null) prioritizedTasks.add(task);
        tasks.put(taskId, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) return;

        Epic epicForUpdate = epics.get(epic.getId());
        epicForUpdate.setName(epic.getName());
        epicForUpdate.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) return;

        int epicId = subTask.getEpicId();

        if (!epics.containsKey(epicId)) return;

        List<Integer> subTasksOfEpic = (epics.get(epicId)).getSubTasks();

        if (!subTasksOfEpic.contains(subTask.getId())) return;

        SubTask subTaskOld = subTasks.get(subTask.getId());

        prioritizedTasks.remove(subTaskOld);
        if (isIntersect(subTask)) {
            prioritizedTasks.add(subTaskOld);
            return;
        } // Новая подзадача пересекается по времени с имеющимися задачами => возвращаем прежнюю на место

        if (subTask.getStartTime() != null) prioritizedTasks.add(subTask);
        subTasks.put(subTask.getId(), subTask);

        Epic epicForUpdate = epics.get(epicId);
        epicForUpdate.setStatus(calculateStatus(epicForUpdate));
        calculateEpicDateTime(epicForUpdate);
    }

    private Status calculateStatus(Epic epic) {
        List<Integer> subTaskList = epic.getSubTasks();
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

    private void calculateEpicDateTime(Epic epic) {
        List<Integer> subTaskList = epic.getSubTasks();
        if (subTaskList.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            return;
        }
        epic.setDuration(null);
        subTaskList.forEach(subtaskId -> {
            LocalDateTime startST = subTasks.get(subtaskId).getStartTime();
            LocalDateTime finishST = subTasks.get(subtaskId).getEndTime();
            Duration durationST = subTasks.get(subtaskId).getDuration();


            if (epic.getStartTime() == null) {
                epic.setStartTime(startST);
                epic.setEndTime(finishST);
                epic.setDuration(durationST);
            } else {
                if (startST.isBefore(epic.getStartTime())) {
                    epic.setStartTime(startST);
                    if (epic.getDuration() == null) {
                        epic.setDuration(durationST);
                    } else {
                        epic.setDuration(epic.getDuration().plus(durationST));
                    }
                } else {
                    epic.setEndTime(finishST);
                    if (epic.getDuration() == null) {
                        epic.setDuration(durationST);
                    } else {
                        epic.setDuration(epic.getDuration().plus(durationST));
                    }
                }
            }
        });
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        Epic epicForDelete = epics.get(id);
        for (Integer subTaskId : epicForDelete.getSubTasks()) {
            prioritizedTasks.remove(subTasks.get(subTaskId));
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);

        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!subTasks.containsKey(id)) return;

        SubTask subTask = subTasks.get(id);
        int epicId = subTask.getEpicId();

        if (!epics.containsKey(epicId)) return;

        prioritizedTasks.remove(subTask);
        Epic epic = epics.get(epicId);
        subTasks.remove(id);
        epic.deleteSubTask(id);
        epic.setStatus(calculateStatus(epic));
        calculateEpicDateTime(epic);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubTasks();
            epic.setStatus(calculateStatus(epic));
            calculateEpicDateTime(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }


}



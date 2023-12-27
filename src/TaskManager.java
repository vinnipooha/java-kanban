import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public Task createTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epic.setStatus(calculateStatus(epic));
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        int epicId = subTask.getEpicId();
        Epic epicForUpdate = epics.get(epicId);
        epicForUpdate.addSubTask(subTask.getId());
        epicForUpdate.setStatus(calculateStatus(epicForUpdate));
        return subTask;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void printList(ArrayList list) {
        for (Object o : list) {
            System.out.println(o);
        }
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public SubTask getSubTaskById(int subTaskId) {
        return subTasks.get(subTaskId);
    }

    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> idSubTasksByEpic = epic.getSubTasks();
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        for (int idSubTask : idSubTasksByEpic) {
            subTasksByEpic.add(subTasks.get(idSubTask));
        }
        return subTasksByEpic;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic epicForUpdate = epics.get(epic.getId());
        epicForUpdate.setName(epic.getName());
        epicForUpdate.setDescription(epic.getDescription());
    }

    public void updateSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
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

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epicForDelete = epics.get(id);
        for (Integer subTaskId : epicForDelete.getSubTasks()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        subTasks.remove(id);
        epic.deleteSubTask(id);
        epic.setStatus(calculateStatus(epic));
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubTasks();
            epic.setStatus(calculateStatus(epic));
        }
    }
}

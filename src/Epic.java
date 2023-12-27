import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subTasks = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubTask(Integer subTaskId) {
        subTasks.add(subTaskId);
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasks;
    }

    public void removeAllSubTasks() {
        subTasks.clear();
    }

    public void deleteSubTask(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }

    @Override
    public String toString() {
        return (getId() + ", \'" + getName() + "\', " +
                "описание: \'" + getDescription() + "\', статус: \'" + getStatus() + "\'");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return (getId() == epic.getId()) &&
                Objects.equals(getName(), epic.getName()) &&
                Objects.equals(getDescription(), epic.getDescription());
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash + getId();
        if (getName() != null) {
            hash = hash + getName().hashCode();
        }
        hash = hash*31;
        if (getDescription() != null) {
            hash = hash + getDescription().hashCode();
        }
        return hash;
    }
}

package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subTasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        setType(Type.EPIC);
    }

    public Epic(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        setType(Type.EPIC);
    }

    public void addSubTask(Integer subTaskId) {
        subTasks.add(subTaskId);
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void removeAllSubTasks() {
        subTasks.clear();
    }

    public void deleteSubTask(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return (getId() + "," + getType() + "," + getName() + "," + status.getStatus() + "," + getDescription()
                + "," + getStartTime() + "," + getDuration());
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
        hash = hash * 31;
        if (getDescription() != null) {
            hash = hash + getDescription().hashCode();
        }
        return hash;
    }
}

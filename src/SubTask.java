public class SubTask extends Task{
    private int epicId;
    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }
    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return (getId() + ", \' " + getName() +"\' (эпик " +epicId + "), описание: \'" +
                getDescription() + "\', статус: \'"+ getStatus() + "\'");
    }
}

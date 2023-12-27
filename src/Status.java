public enum Status{
    NEW("Новая"),
    IN_PROGRESS("В процессе"),
    DONE("Завершена");

private String string;
Status(String name) {
    string = name;
}
    @Override
    public String toString() {
        return string;
    }
}

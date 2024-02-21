package model;

public enum Status{
    NEW("Новая"),
    IN_PROGRESS("В процессе"),
    DONE("Завершена");

private final String status;
Status(String name) {
    status = name;
}
   @Override
   public String toString() {
        return status;
    }
}

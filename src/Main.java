import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = taskManager.createTask(new Task("Задача", "Изучить теорию"));
        Task task2 = taskManager.createTask(new Task("Задача", "Прочитать ТЗ"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик", "Написать проект"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик", "Сдать проект"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("Подзадача", "Создать необходимые классы", epic1.getId()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Подзадача", "Написать методы, согласно ТЗ", epic1.getId()));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Подзадача", "Отправить изменения на GitHub", epic1.getId()));

        printAllTasks(taskManager);

       taskManager.getTaskById(1);
       taskManager.getTaskById(2);
       taskManager.getEpicById(3);
       taskManager.getEpicById(4);
       taskManager.getSubTaskById(5);
       taskManager.getSubTaskById(6);
       taskManager.getSubTaskById(7);

       printHistory(taskManager);

        taskManager.getSubTaskById(5);
        taskManager.getTaskById(2);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(6);
        taskManager.getTaskById(1);
        taskManager.getEpicById(4);
        taskManager.getEpicById(3);

       printHistory(taskManager);

       taskManager.deleteTaskById(2);

       printHistory(taskManager);

       taskManager.deleteEpicById(3);

       printHistory(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubTasksByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }

    private static void printHistory (TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}

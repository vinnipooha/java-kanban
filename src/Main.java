import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(15);
        Task task1 = taskManager.createTask(new Task("Задача", "Изучить теорию", now, duration));
        Task task2 = taskManager.createTask(new Task("Задача", "Прочитать ТЗ", now.plusMinutes(20),duration));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик", "Написать проект"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик", "Сдать проект"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("Подзадача", "Создать необходимые классы", epic1.getId(), now.plusMinutes(40), duration));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Подзадача", "Написать методы согласно ТЗ", epic1.getId(), now.plusMinutes(60), duration));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Подзадача", "Отправить изменения на GitHub", epic1.getId(), now.plusMinutes(80), duration));

        taskManager.getSubTaskById(5);
        taskManager.getTaskById(2);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(6);
        taskManager.getTaskById(1);
        taskManager.getEpicById(4);
        taskManager.getEpicById(3);

        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(4);

        taskManager.updateTask(new Task(1, "Задача", "Изучить теорию", Status.DONE, now, duration));
        taskManager.updateSubTask(new SubTask(6, "Подзадача", "Написать методы согласно ТЗ", Status.DONE, 3, now.plusMinutes(60), duration));

        printAllTasks(taskManager);

        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(Paths.get("sourses/tasks.csv"));

        printAllTasks(taskManager1);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        manager.getAllTasks().forEach(System.out::println);

        System.out.println("Эпики:");
        manager.getAllEpics().forEach(epic -> {
            System.out.println(epic);
            manager.getSubTasksByEpic(epic.getId()).stream().map(task -> "--> " + task).forEach(System.out::println);
        });
        System.out.println("Подзадачи:");
        manager.getAllSubTasks().forEach(System.out::println);

        System.out.println("История:");
        manager.getHistory().forEach(System.out::println);

        System.out.println("Приоритет:");
        manager.getPrioritizedTasks().forEach(System.out::println);
        System.out.println();
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История:");
        manager.getHistory().forEach(System.out::println);
        System.out.println();
    }
}

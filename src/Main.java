public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = taskManager.createTask(new Task("Задача 1", "Изучить теорию", Status.NEW));
        Task task2 = taskManager.createTask(new Task("Задача 2", "Прочитать ТЗ", Status.NEW));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Написать проект"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", "Сдать проект"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("Подзадача 1", "Создать необходимые классы", Status.NEW, epic1.getId()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Подзадача 2", "Написать методы, согласно ТЗ", Status.NEW, epic1.getId()));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Подзадача 1", "Отправить изменения на GitHub", Status.NEW, epic2.getId()));

        taskManager.printList(taskManager.getAllTasks());
        taskManager.printList(taskManager.getAllEpics());
        taskManager.printList(taskManager.getAllSubTasks());

        taskManager.updateTask(new Task(task1.getId(), "Задача 1", "Изучить теорию", Status.IN_PROGRESS));
        taskManager.updateTask(new Task(task2.getId(), "Задача 2", "Прочитать ТЗ", Status.DONE));

        taskManager.updateSubTask(new SubTask(subTask1.getId(), "Подзадача 1", "Создать необходимые классы", Status.DONE, epic1.getId()));
        taskManager.updateSubTask(new SubTask(subTask2.getId(), "Подзадача 2", "Написать методы, согласно ТЗ", Status.DONE, epic1.getId()));
        taskManager.updateSubTask(new SubTask(subTask3.getId(), "Подзадача 1", "Отправить изменения на GitHub", Status.IN_PROGRESS, epic2.getId()));

        System.out.println();
        taskManager.printList(taskManager.getAllTasks());
        taskManager.printList(taskManager.getAllEpics());
        taskManager.printList(taskManager.getAllSubTasks());

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic2.getId());

        System.out.println();
        taskManager.printList(taskManager.getAllTasks());
        taskManager.printList(taskManager.getAllEpics());
        taskManager.printList(taskManager.getAllSubTasks());


    }
}

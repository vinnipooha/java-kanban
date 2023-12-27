public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = taskManager.createTask(new Task("Задача", "Изучить теорию"));
        Task task2 = taskManager.createTask(new Task("Задача", "Прочитать ТЗ"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик", "Написать проект"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик", "Сдать проект"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("Подзадача", "Создать необходимые классы", epic1.getId()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Подзадача", "Написать методы, согласно ТЗ", epic1.getId()));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Подзадача", "Отправить изменения на GitHub", epic2.getId()));

        taskManager.printList(taskManager.getAllTasks());
        taskManager.printList(taskManager.getAllEpics());
        taskManager.printList(taskManager.getAllSubTasks());

        taskManager.updateTask(new Task(task1.getId(), "Задача", "Изучить теорию", Status.IN_PROGRESS));
        taskManager.updateTask(new Task(task2.getId(), "Задача", "Прочитать ТЗ", Status.DONE));

        taskManager.updateSubTask(new SubTask(subTask1.getId(), "Подзадача", "Создать необходимые классы", Status.DONE, epic1.getId()));
        taskManager.updateSubTask(new SubTask(subTask2.getId(), "Подзадача", "Написать методы, согласно ТЗ", Status.DONE, epic1.getId()));
        taskManager.updateSubTask(new SubTask(subTask3.getId(), "Подзадача", "Отправить изменения на GitHub", Status.IN_PROGRESS, epic2.getId()));

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

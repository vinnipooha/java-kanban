package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void beforeEach() throws IOException {
        taskManager = Managers.getDefaultInMemoryManager();
        taskServer = new HttpTaskServer(taskManager);
        taskServer.startServer();
    }

    @AfterEach
    public void afterEach() {
        taskServer.stopServer();
    }

    @Test
    void Task_GetRequestToPathTasksShouldReturnListWithSingleTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Task task = taskManager.createTask(new Task("Task", "T_descr", startTime, duration));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> tasksList = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());
            assertEquals(200, response.statusCode(), "GET-запрос на получение списка задач не работает");
            assertNotNull(tasksList, "GET-запрос на получение списка задач ничего не возвращает");
            assertEquals(1, tasksList.size(), "В списке tasksList должна быть одна задача");
            assertEquals(task, tasksList.getFirst(), "Задача из списка taskList должна быть равна task");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Task_GetRequestToPathTasksIdShouldReturnSingleTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Task task = taskManager.createTask(new Task("Task", "T_descr", startTime, duration));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task taskById = gson.fromJson(response.body(), Task.class);
            assertEquals(200, response.statusCode(), "GET-запрос на получение задачи по id не работает");
            assertNotNull(taskById, "GET-запрос на получение задачи по id ничего не возвращает");
            assertEquals(task, taskById, "Задача taskById должна быть равна task");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Task_PostRequestToPathWithoutQueryShouldCreateTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);

        String taskToJson = gson.toJson(new Task("Task", "T_descr", startTime, duration));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task taskCreate = gson.fromJson(response.body(), Task.class);
            assertEquals(201, response.statusCode(), "POST-запрос на создание задачи не работает");
            assertNotNull(taskCreate, "POST-запрос на создание задачи ничего не возвращает");
            assertEquals(1, taskCreate.getId(), "id задачи должен быть равен 1");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Task_PostRequestToPathWithQueryShouldUpdateTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Task task = taskManager.createTask(new Task("Task", "T_descr", startTime, duration));
        Task taskToUpdate = new Task(1, "Task_Up", "T_descr_Up", Status.IN_PROGRESS, startTime.plusMinutes(10), duration);
        String taskToJson = gson.toJson(taskToUpdate);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();
        assertEquals(1, taskManager.getAllTasks().size(), "Размер списка задач должен быть равен 1");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseStr = response.body();
            assertEquals(201, response.statusCode(), "POST-запрос на обновление задачи не работает");
            assertEquals(1, taskManager.getAllTasks().size(), "Размер списка задач должен быть равен 1");
            assertEquals("Задача c id = 1 изменена", responseStr, "Ответ сервера не совпадает с ожидаемым");
            assertEquals(taskToUpdate, taskManager.getTaskById(1), "Обновление задачи работает некорректно");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Task_DeleteRequest() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Task task = taskManager.createTask(new Task("Task", "T_descr", startTime, duration));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        assertEquals(1, taskManager.getAllTasks().size(), "Размер списка задач должен быть равен 1");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseStr = response.body();
            assertEquals(200, response.statusCode(), "Запрос на удаление задачи не работает");
            assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
            assertEquals("Задача c id = 1 удалена", responseStr, "Ответ сервера не совпадает с ожидаемым");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void SubTask_GetRequestToPathSubtasksShouldReturnListWithSingleSubTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Epic epic = taskManager.createEpic(new Epic("Epic", "E_descr"));
        SubTask subTask = taskManager.createSubTask(
                new SubTask("SubTask", "ST_descr", 1, startTime, duration));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<SubTask> subTasksList = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
            }.getType());
            assertEquals(200, response.statusCode(), "GET-запрос на получение списка подзадач не работает");
            assertNotNull(subTasksList, "GET-запрос на получение списка подзадач ничего не возвращает");
            assertEquals(1, subTasksList.size(), "В списке subTasksList должна быть одна подзадача");
            assertEquals(subTask, subTasksList.getFirst(), "Подзадача из списка subTaskList должна быть равна subTask");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void SubTask_GetRequestToPathSubtasksIdShouldReturnSingleSubTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Epic epic = taskManager.createEpic(new Epic("Epic", "E_descr"));
        SubTask subTask = taskManager.createSubTask(
                new SubTask("SubTask", "ST_descr", 1, startTime, duration));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            SubTask subTaskById = gson.fromJson(response.body(), SubTask.class);
            assertEquals(200, response.statusCode(), "GET-запрос на получение подзадачи по id не работает");
            assertNotNull(subTaskById, "GET-запрос на получение подзадачи по id ничего не возвращает");
            assertEquals(subTask, subTaskById, "Подзадача subTaskById должна быть равна subTask");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void SubTask_PostRequestToPathWithoutQueryShouldCreateSubTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Epic epic = taskManager.createEpic(new Epic("Epic", "E_descr"));
        SubTask subTask = new SubTask("SubTask", "ST_descr", 1, startTime, duration);
        String subTaskToJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskToJson))
                .build();
        assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач должен быть пуст");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            SubTask subTaskCreate = gson.fromJson(response.body(), SubTask.class);
            assertEquals(201, response.statusCode(), "POST-запрос на создание подзадачи не работает");
            assertNotNull(subTaskCreate, "POST-запрос на создание подзадачи ничего не возвращает");
            assertEquals(2, subTaskCreate.getId(), "id подзадачи должен быть равен 2");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void SubTask_PostRequestToPathWithQueryShouldUpdateSubTask() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);

        Epic epic = taskManager.createEpic(new Epic("Epic", "E_descr"));
        SubTask subTask = taskManager.createSubTask(new SubTask("SubTask", "ST_descr", 1, startTime, duration));

        SubTask subTaskToUpdate = new SubTask(2, "SubTask_Up", "ST_descr_Up", Status.IN_PROGRESS, 1, startTime.plusMinutes(10), duration);
        String subTaskToJson = gson.toJson(subTaskToUpdate);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskToJson))
                .build();
        assertEquals(1, taskManager.getAllSubTasks().size(), "Размер списка подзадач должен быть равен 1");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseStr = response.body();
            assertEquals(201, response.statusCode(), "POST-запрос на обновление подзадачи не работает");
            assertEquals(1, taskManager.getAllSubTasks().size(), "Размер списка подзадач должен быть равен 1");
            assertEquals("Подзадача c id = 2 изменена", responseStr, "Ответ сервера не совпадает с ожидаемым");
            assertEquals(subTaskToUpdate, taskManager.getSubTaskById(2), "Обновление подзадачи работает некорректно");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void SubTask_DeleteRequest() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        taskManager.createEpic(new Epic("Epic", "E_descr"));
        taskManager.createSubTask(new SubTask("SubTask", "ST_descr", 1, startTime, duration));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        assertEquals(1, taskManager.getAllSubTasks().size(), "Размер списка подзадач должен быть равен 1");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseStr = response.body();
            assertEquals(200, response.statusCode(), "Запрос на удаление подзадачи не работает");
            assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач должен быть пуст");
            assertEquals("Подзадача c id = 2 удалена", responseStr, "Ответ сервера не совпадает с ожидаемым");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Epic_GetRequestToPathEpicsShouldReturnListWithSingleEpic() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "E_descr"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Epic> EpicList = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
            }.getType());
            assertEquals(200, response.statusCode(), "GET-запрос на получение списка эпиков не работает");
            assertNotNull(EpicList, "GET-запрос на получение списка эпиков ничего не возвращает");
            assertEquals(1, EpicList.size(), "В списке EpicList должен быть один эпик");
            assertEquals(epic, EpicList.getFirst(), "Эпик из списка EpicList должен быть равен epic");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Epic_GetRequestToPathEpicsIdShouldReturnSingleEpic() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Epic epic = taskManager.createEpic(new Epic("Epic", "E_descr"));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Epic epicById = gson.fromJson(response.body(), Epic.class);
            assertEquals(200, response.statusCode(), "GET-запрос на получение эпика по id не работает");
            assertNotNull(epicById, "GET-запрос на получение эпика по id ничего не возвращает");
            assertEquals(epic, epicById, "Эпик epicById должен быть равен epic");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Epic_GetRequestToPathEpicsIdSubtasksShouldReturnListSubTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Epic epic = taskManager.createEpic(new Epic("Epic", "E_descr"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("SubTask1", "ST1_descr",
                1, startTime, duration));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("SubTask2", "ST2_descr",
                1, startTime.plusMinutes(10), duration));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<SubTask> subTaskByEpic= gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
            }.getType());
            assertEquals(200, response.statusCode(), "GET-запрос на получение списка подзадач эпика не работает");
            assertNotNull(subTaskByEpic, "GET-запрос на получение списка подзадач эпика ничего не возвращает");
            assertEquals(2, subTaskByEpic.size(), "В списке у эпика должно быть две подзадачи");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Epic_PostRequestToPathWithoutQueryShouldCreateEpic() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Epic epic = new Epic("Epic", "E_descr");

        String epicToJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicToJson))
                .build();
        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков должен быть пуст");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Epic epicCreate = gson.fromJson(response.body(), Epic.class);
            assertEquals(201, response.statusCode(), "POST-запрос на создание эпика не работает");
            assertNotNull(epicCreate, "POST-запрос на создание эпика ничего не возвращает");
            assertEquals(1, epicCreate.getId(), "id эпика должен быть равен 1");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Epic_PostRequestToPathWithQueryShouldUpdateEpic() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);

        Epic epic = taskManager.createEpic(new Epic("Epic", "E_descr"));
        Epic epicToUpdate = new Epic (1, "Epic_Up", "E_descr_Up");

        String epicToJson = gson.toJson(epicToUpdate);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicToJson))
                .build();
        assertEquals(1, taskManager.getAllEpics().size(), "Размер списка эпиков должен быть равен 1");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseStr = response.body();
            assertEquals(201, response.statusCode(), "POST-запрос на обновление эпика не работает");
            assertEquals(1, taskManager.getAllEpics().size(), "Размер списка эпиков должен быть равен 1");
            assertEquals("Эпик с id = 1 изменен", responseStr, "Ответ сервера не совпадает с ожидаемым");
            assertEquals(epicToUpdate, taskManager.getEpicById(1), "Обновление эпика работает некорректно");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Epic_DeleteRequest() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Epic epic = taskManager.createEpic(new Epic("Epic", "E_descr"));
        SubTask subTask = taskManager.createSubTask(new SubTask("ST", "ST_descr", 1, startTime, duration));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        assertEquals(1, taskManager.getAllEpics().size(), "Размер списка эпиков должен быть равен 1");
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseStr = response.body();
            assertEquals(200, response.statusCode(), "Запрос на удаление эпика не работает");
            assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков должен быть пуст");
            assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач должен быть пуст");
            assertEquals("Эпик c id = 1 удален", responseStr, "Ответ сервера не совпадает с ожидаемым");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void History_GetRequest() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Task task1 = taskManager.createTask(new Task("Task1", "T_descr1", startTime, duration));
        Task task2 = taskManager.createTask(new Task("Task2", "T_descr2", startTime.plusMinutes(10), duration));
        Task task3 = taskManager.createTask(new Task("Task3", "T_descr3", startTime.plusMinutes(20), duration));
        taskManager.getTaskById(1);
        taskManager.getTaskById(3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> historyList = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());
            assertEquals(200, response.statusCode(), "GET-запрос на получение списка просмотров не работает");
            assertNotNull(historyList, "GET-запрос на получение списка просмотров ничего не возвращает");
            assertEquals(2, historyList.size(), "В списке просмотров должны быть две задачи");
            assertEquals(task3, historyList.getFirst(), "Первая задача из списка просмотров должна быть равна task3");
            assertEquals(task1, historyList.getLast(), "Последняя задача из списка просмотров должна быть равна task1");
            assertFalse(historyList.contains(task2), "В списке просмотров не должно быть task2");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Test
    void Prioritized_GetRequest() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(9);
        Task task1 = taskManager.createTask(new Task("Task1", "T_descr1", startTime, duration));
        Task task2 = taskManager.createTask(new Task("Task2", "T_descr2", startTime.plusMinutes(10), duration));
        Task task3 = taskManager.createTask(new Task("Task3", "T_descr3", startTime.minusMinutes(5), duration));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> prioritizedList = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());
            assertEquals(200, response.statusCode(), "GET-запрос на получение списка приоритета не работает");
            assertNotNull(prioritizedList, "GET-запрос на получение списка приорита ничего не возвращает");
            assertEquals(2, prioritizedList.size(), "В списке приоритета должны быть две задачи");
            assertEquals(task1, prioritizedList.getFirst(), "Первая задача из списка приоритета должна быть равна task1");
            assertEquals(task2, prioritizedList.getLast(), "Последняя задача из списка приоритета должна быть равна task2");
            assertFalse(prioritizedList.contains(task3), "В списке приоритета не должно быть task3");

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}

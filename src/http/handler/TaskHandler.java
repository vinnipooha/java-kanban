package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        gson = HttpTaskServer.getGson();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String[] splitPath = path.split("/");

        String response;
        switch (exchange.getRequestMethod()) {
            case "GET":
                if (splitPath.length == 2) {
                    response = gson.toJson(taskManager.getAllTasks());
                    writeResponse(exchange, response, 200);
                }
                if (splitPath.length == 3) {
                    int taskId = Integer.parseInt(splitPath[2]);
                    response = gson.toJson(taskManager.getTaskById(taskId));
                    if (response.equals("null")) {
                        writeResponse(exchange, "Задачи с id: " + taskId + " не существует", 404);
                    } else {

                        writeResponse(exchange, response, 200);
                    }
                }
                break;
            case "POST":
                if (query == null) {
                    Task task = gson.fromJson(body, Task.class);
                    response = gson.toJson(taskManager.createTask(task));
                    if (response.equals("null")) {
                        writeResponse(exchange, "Задача пересекается с существующими", 406);
                    } else {
                        writeResponse(exchange, response, 201);
                    }

                } else {
                    String[] querySplit = query.split("=");
                    int taskId = Integer.parseInt(querySplit[1]);
                    Task task = gson.fromJson(body, Task.class);
                    taskManager.updateTask(task);
                    writeResponse(exchange, "Задача c id = " + taskId + " изменена", 201);
                }
                break;
            case "DELETE":
                if (query == null) {
                    writeResponse(exchange, "В параметрах пути не указан id задачи для удаления", 400);
                } else {
                    String[] querySplit = query.split("=");
                    int taskId = Integer.parseInt(querySplit[1]);
                    taskManager.deleteTaskById(taskId);
                    writeResponse(exchange, "Задача c id = " + taskId + " удалена", 200);
                }
                break;
            default:
                writeResponse(exchange, "Вы использовали некорректный метод запроса", 400);
        }
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isEmpty()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            exchange.sendResponseHeaders(responseCode, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseString.getBytes(DEFAULT_CHARSET));
            }
        }
    }


}


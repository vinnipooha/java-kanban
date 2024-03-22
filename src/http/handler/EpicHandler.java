package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.HttpTaskServer;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EpicHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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
                    response = gson.toJson(taskManager.getAllEpics());
                    writeResponse(exchange, response, 200);
                    return;
                }
                if (splitPath.length == 3) {
                    int epicId = Integer.parseInt(splitPath[2]);
                    response = gson.toJson(taskManager.getEpicById(epicId));
                    if (response.equals("null")) {
                        writeResponse(exchange, "Эпика с id = " + epicId + " не существует", 404);
                    } else {
                        writeResponse(exchange, response, 200);
                    }
                }
                if (splitPath.length == 4) {
                    int epicId = Integer.parseInt(splitPath[2]);
                    response = gson.toJson(taskManager.getSubTasksByEpic(epicId));
                    if (response.equals("null")) {
                        writeResponse(exchange, "Эпика с id = " + epicId + " не существует", 404);
                    } else {
                        writeResponse(exchange, response, 200);
                    }
                }
                break;
            case "POST":
                if (query == null) {
                    Epic epic = gson.fromJson(body, Epic.class);
                    String responseJson = gson.toJson(taskManager.createEpic(epic));
                    writeResponse(exchange, responseJson, 201);

                } else {
                    String[] querySplit = query.split("=");
                    int epicId = Integer.parseInt(querySplit[1]);
                    Epic epic = gson.fromJson(body, Epic.class);
                    taskManager.updateEpic(epic);
                    writeResponse(exchange, "Эпик с id = " + epicId + " изменен", 201);
                }
                break;
            case "DELETE":
                if (query == null) {
                    writeResponse(exchange, "В параметрах пути не указан id эпика для удаления", 400);
                } else {
                    String[] querySplit = query.split("=");
                    int epicId = Integer.parseInt(querySplit[1]);
                    taskManager.deleteEpicById(epicId);
                    writeResponse(exchange, "Эпик c id = " + epicId + " удален", 200);
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
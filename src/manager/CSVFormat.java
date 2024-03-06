package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVFormat {
    static Task taskFromString(String value) throws RuntimeException {
        String[] columns = value.split(",");
        int id = Integer.parseInt(columns[0]);

        Status status = null;
        Status[] statuses = Status.values();
        for (int i = 0; i < statuses.length && status == null; i++) {
            if (statuses[i].getStatus().equals(columns[3])) {
                status = statuses[i];
            }
        }
        if (status == null) throw new RuntimeException("Статус не найден: " + columns[3]);

        Type type = Type.valueOf(columns[1]);

        Task task;

        switch (type) {
            case TASK:
                task = new Task(id, columns[2], columns[4], status, LocalDateTime.parse(columns[6]),
                        Duration.parse(columns[7]));
                break;

            case EPIC:
                task = new Epic(id, columns[2], columns[4]);
                task.setStatus(status);
                if (!columns[6].equals("null")) {
                    task.setStartTime(LocalDateTime.parse(columns[6]));
                } else {
                    task.setStartTime(null);
                }
                if (!columns[7].equals("null")) {
                    task.setDuration(Duration.parse(columns[7]));
                } else {
                    task.setDuration(null);
                }
                break;

            case SUBTASK:
                task = new SubTask(id, columns[2], columns[4], status, Integer.parseInt(columns[8]), LocalDateTime.parse(columns[6]),
                        Duration.parse(columns[7]));
                break;

            default:
                throw new ManagerSaveException(("Неизвестный тип объекта " + type));
        }

        return task;
    }

    static String historyToString(HistoryManager historyManager) {
        List<Task> historyList = historyManager.getHistory();
        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (Task task : historyList) {
            if (counter < historyList.size() - 1) {
                builder.append(task.getId());
                builder.append(",");
            } else {
                builder.append(task.getId());
            }
            counter++;
        }
        return builder.toString();
    }

    static List<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        List<Integer> list = new ArrayList<>();
        for (int i = split.length - 1; i >= 0; i--) {
            list.add(Integer.valueOf(split[i]));
        }
        return list;
    }
}

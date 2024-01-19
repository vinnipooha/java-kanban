package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    protected List<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (historyList.size() <= 10) {
            historyList.add(task);
        } else {
            historyList.removeFirst();
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory(){
        return historyList;
    }
}

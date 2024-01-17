import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    protected ArrayList<Task> historyList = new ArrayList<>();

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
    public ArrayList<Task> getHistory(){
        return historyList;
    }
}

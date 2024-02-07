package manager;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        int id = task.getId();
        remove(id);
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (!getHistoryMap().containsKey(id)) {
            return;
        }
        Map<Integer, Node> historyMap = getHistoryMap();
        Node nodeForDelete = historyMap.get(id);
        removeNode(nodeForDelete);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.setNext(newNode);
        int id = task.getId();
        historyMap.put(id, newNode);
    }

    public List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add(current.getTask());
            current = current.getNext();
        }
        return list;
    }

    public void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();
        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
            node.setNext(null);
        }
        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
            node.setPrev(null);
        }
        node.setTask(null);
    }

    public Map<Integer, Node> getHistoryMap() {
        return historyMap;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }
}



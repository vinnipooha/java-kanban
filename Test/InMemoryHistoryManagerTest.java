import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void BeforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldDeleteThePreviousTaskView() {
        historyManager = Managers.getDefaultHistory();

        Task task = new Task(1, "T1", "descr1");
        Epic epic = new Epic(2, "Epic", "Epic.descr");
        SubTask subTask = new SubTask(3, "ST", "STdescr", Status.NEW, 2);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);

        assertEquals(3, historyManager.getHistory().size(), "Добавление просмотров в историю не работает");

        historyManager.add(epic);
        assertEquals(3, historyManager.getHistory().size(), "Удаление предыдущих просмотров из истории не работает");

        assertEquals(1, historyManager.getHistory().get(0).getId(), "Порядок просмотра задач не сохраняется");
        assertEquals(3, historyManager.getHistory().get(1).getId(), "Порядок просмотра задач не сохраняется");
        assertEquals(2, historyManager.getHistory().get(2).getId(), "Порядок просмотра задач не сохраняется");

        historyManager.remove(3);
        assertEquals(2, historyManager.getHistory().size(), "Удаление просмотров не работает");
        assertFalse(historyManager.getHistory().contains(subTask), "");
        assertEquals(1, historyManager.getHistory().get(0).getId(), "task должна быть первым элементом в списке");
        assertEquals(2, historyManager.getHistory().get(1).getId(), "epic должен быть вторым элементом в списке");
    }

    @Test
    void shouldRemoveNodeForId() {
        Task task = new Task(1, "T1", "descr1");
        Epic epic = new Epic(2, "Epic", "Epic.descr");
        SubTask subTask = new SubTask(3, "ST", "STdescr", Status.NEW, 2);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);

        historyManager.remove(2);
        assertEquals(2, historyManager.getHistory().size(), "Удаление просмотров не работает");
        assertFalse(historyManager.getHistory().contains(epic), "Удаление элемента работает некорректно");
        assertEquals(1, historyManager.getHistory().get(0).getId(), "task должна быть первым элементом в списке");
        assertEquals(3, historyManager.getHistory().get(1).getId(), "subTask должна быть вторым элементом в списке");
    }

        @Test
    void shouldAddNodeInOrder() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task task = new Task(1, "T1", "descr1");
        Epic epic = new Epic(2, "Epic", "Epic.descr");
        SubTask subTask = new SubTask(3, "ST", "STdescr", Status.NEW, 2);

        inMemoryHistoryManager.linkLast(task);
        inMemoryHistoryManager.linkLast(epic);
        inMemoryHistoryManager.linkLast(subTask);

        assertEquals(task, inMemoryHistoryManager.getHead().getTask(), "Добавление задач по порядку не работает");
        assertEquals(subTask, inMemoryHistoryManager.getTail().getTask(), "Добавление задач по порядку не работает");

        inMemoryHistoryManager.linkLast(task);

        List<Task> list = inMemoryHistoryManager.getTasks();
        assertEquals(4, list.size(), "Добавление задач в список не работает");
        assertEquals(task, inMemoryHistoryManager.getTail().getTask(), "Последним элементом должна быть task");
    }

    @Test
    void ShouldChangeHeadWhenDeleteFirstNode() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task task = new Task(1, "T1", "descr1");
        Epic epic = new Epic(2, "Epic", "Epic.descr");
        SubTask subTask = new SubTask(3, "ST", "STdescr", Status.NEW, 2);

        inMemoryHistoryManager.linkLast(task);
        inMemoryHistoryManager.linkLast(epic);
        inMemoryHistoryManager.linkLast(subTask);

        inMemoryHistoryManager.removeNode(inMemoryHistoryManager.getHead());
        List<Task> list = inMemoryHistoryManager.getTasks();
        assertEquals(epic, list.getFirst(), "При удалении head первый элемент списка просмотров не меняется");
    }

    @Test
    void ShouldChangeTailWhenDeleteLastNode() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task task = new Task(1, "T1", "descr1");
        Epic epic = new Epic(2, "Epic", "Epic.descr");
        SubTask subTask = new SubTask(3, "ST", "STdescr", Status.NEW, 2);

        inMemoryHistoryManager.linkLast(task);
        inMemoryHistoryManager.linkLast(epic);
        inMemoryHistoryManager.linkLast(subTask);

        inMemoryHistoryManager.removeNode(inMemoryHistoryManager.getTail());
        List<Task> list = inMemoryHistoryManager.getTasks();
        assertEquals(epic, list.getLast(), "При удалении tail последний элемент списка просмотров не меняется");
    }

    @Test
    void removeNode() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

        Task task = new Task(1, "T1", "descr1");
        Epic epic = new Epic(2, "Epic", "Epic.descr");
        SubTask subTask = new SubTask(3, "ST", "STdescr", Status.NEW, 2);

        inMemoryHistoryManager.linkLast(task);
        inMemoryHistoryManager.linkLast(epic);
        inMemoryHistoryManager.linkLast(subTask);

        Map<Integer, Node> historyMap = inMemoryHistoryManager.getHistoryMap();

        Node nodeForDelete = historyMap.get(2);
        String prev = nodeForDelete.getPrev().getTask().getName();
        String next = nodeForDelete.getNext().getTask().getName();
        assertEquals("T1", prev);
        assertEquals("ST", next);

        inMemoryHistoryManager.removeNode(nodeForDelete);

        List<Task> list1 = inMemoryHistoryManager.getTasks();
        assertEquals(2, list1.size(), "Удаление элементов не работает");
        assertEquals(task, list1.getFirst(), "Первым элементом должна быть task");
        assertEquals(subTask, list1.getLast(), "Вторым элементом долна быть subTask");
    }
}
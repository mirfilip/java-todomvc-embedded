import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application implements Controller {

    private final InMemoryRepository repository;

    public Application(InMemoryRepository repo) {
        repository = repo;
        this.insertDummyTodos();
    }

    public Application() {
        this(new InMemoryRepository());
    }

    private void insertDummyTodos() {
        /**
         * Done just to have a different times of Todo insertion
         */
        try {
            repository.save(new Todo("Learn Servlets"));
            Thread.sleep(1000);
            repository.save(new Todo(new Todo("Completed"), Todo.Status.COMPLETED));
        } catch (InterruptedException e) {
        } finally {
            System.out.println("Inserted " + repository.count() + " dummy todos");
        }
    }

    public Map<String, Collection<Todo>> handle(String requestMethod, String requestUri, Map<String, String[]> params) {
        String command = parseCommand(requestUri);
        return dispatchControl(requestMethod, command, params);
    }

    Map<String, Collection<Todo>> dispatchControl(String requestMethod, String command, Map<String, String[]> params) {
        Map<String, Collection<Todo>> attributes = new HashMap<>();

        // TODO: No switch statement with boolean, how to do that cleanly?
        switch (command) {
            case "index":
                handleIndex(attributes);
                break;
            case "new":
                handleCreate(params.get("new-todo")[0]);
                break;
            case "toggle":
                handleToggle(params.get("todo-id")[0]);
                break;
            case "delete":
                handleDelete(params.get("todo-id")[0]);
                break;
            case "clear":
                handleClear();
                break;
        }

        return attributes;
    }

    private void handleClear() {
        repository.deleteBy(t -> t.getStatus() == Todo.Status.COMPLETED);
    }

    private void handleIndex(Map<String, Collection<Todo>> attributes) {
        Collection<Todo> all = repository.findAll();
        List<Todo> completed = repository.findAllByStatus(entry -> entry.getValue().getStatus() == Todo.Status.COMPLETED);

        attributes.put("todos", all);
        attributes.put("completed", completed);
    }

    private void handleCreate(String s) {
        Todo newTodo = new Todo(s);

        repository.save(newTodo);
    }

    private void handleDelete(String s) {
        Long todoId = Long.parseLong(s);
        repository.delete(todoId);
    }

    private void handleToggle(String s) {
        Long todoId = Long.parseLong(s);
        Todo todo = repository.findOne(todoId);
        Todo.Status toggledStatus = todo.getStatus() == Todo.Status.ACTIVE ? Todo.Status.COMPLETED : Todo.Status.ACTIVE;

        Todo changedTodo = new Todo(todo, toggledStatus);
        repository.save(changedTodo);
    }

    String parseCommand(String requestURI) {
        int lastSlashIdx = requestURI.lastIndexOf("/");
        int lastDotIdx = requestURI.lastIndexOf(".");

        return requestURI.substring(lastSlashIdx + 1, lastDotIdx);
    }
}

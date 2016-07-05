import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontController {

    private final InMemoryRepository repository;

    public FrontController(InMemoryRepository repo) {
        repository = repo;
        this.insertDummyTodos();
    }

    public FrontController() {
        repository = new InMemoryRepository();
        System.out.println("Created empty InMemoryRepository repository");
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

    public Map<String, Collection<Todo>> handle(String requestUri, Map<String, String[]> params) {
        String command = parseCommand(requestUri);
        return dispatchControl(command, params);
    }

    Map<String, Collection<Todo>> dispatchControl(String command, Map<String, String[]> params) {
        Map<String, Collection<Todo>> attributes = new HashMap<>();

        switch (command) {
            case "index":
                Collection<Todo> all = repository.findAll();
                List<Todo> completed = repository.findAllByStatus(entry -> entry.getValue().getStatus() == Todo.Status.COMPLETED);

                attributes.put("todos", all);
                attributes.put("completed", completed);
                break;
            case "new":
                String newTodoTitle = params.get("new-todo")[0];
                Todo newTodo = new Todo(newTodoTitle);

                repository.save(newTodo);
                break;
            case "toggle": {
                Long todoId = Long.parseLong(params.get("todo-id")[0]);
                Todo todo = repository.findOne(todoId);
                Todo.Status toggledStatus = todo.getStatus() == Todo.Status.ACTIVE ? Todo.Status.COMPLETED : Todo.Status.ACTIVE;

                Todo changedTodo = new Todo(todo, toggledStatus);
                repository.save(changedTodo);
                break;
            }
            case "delete": {
                Long todoId = Long.parseLong(params.get("todo-id")[0]);
                repository.delete(todoId);
                break;
            }
            case "clear":
                repository.deleteBy(t -> t.getStatus() == Todo.Status.COMPLETED);
                break;
        }

        return attributes;
    }

    String parseCommand(String requestURI) {
        int lastSlashIdx = requestURI.lastIndexOf("/");
        int lastDotIdx = requestURI.lastIndexOf(".");

        return requestURI.substring(lastSlashIdx + 1, lastDotIdx);
    }
}

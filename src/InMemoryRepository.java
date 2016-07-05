import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InMemoryRepository implements Repository {
    private Map<Long, Todo> internalMap;

    public InMemoryRepository() {
        this(Collections.emptyList());
    }

    public InMemoryRepository(Collection<Todo> values) {
        this.internalMap = new LinkedHashMap<>();
        values.stream().forEach(p -> internalMap.put(p.getId(), p));
    }

    @Override
    public long count() {
        return (long) this.internalMap.size();
    }

    @Override
    public List<Todo> findAll() {
        if (this.internalMap.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<Todo> todosArray = new ArrayList<>(this.internalMap.values());
            Collections.reverse(todosArray);

            return Collections.unmodifiableList(todosArray);
        }
    }

    @Override
    public List<Todo> findAllByStatus(Predicate<Map.Entry<Long, Todo>> entryPredicate) {
        return internalMap.entrySet()
                .stream()
                .filter(entryPredicate)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Todo findOne(Long id) {
        return this.internalMap.get(id);
    }

    @Override
    public Todo save(Todo entity) {
        this.internalMap.put(entity.getId(), entity);

        return entity;
    }

    @Override
    public int delete(Todo entity) {
        return this.delete(entity.getId());
    }

    @Override
    public int delete(Long id) {
        Todo removedItem = this.internalMap.remove(id);
        return null == removedItem ? 0 : 1;
    }

    @Override
    public int delete(Collection<Todo> entities) {
        int itemsDeleted = 0;

        for (Todo entity : entities) {
            itemsDeleted += this.delete(entity.getId());
        }

        return itemsDeleted;
    }

    @Override
    public int deleteBy(Predicate<Todo> predicate) {
        Collection<Todo> allTodos = this.findAll();

        return (int) allTodos.stream()
                .filter(predicate)
                .map(todo -> delete(todo.getId()))
                .filter(i -> i == 1).count();
    }

    @Override
    public void deleteAll() {
        this.internalMap.clear();
    }

    @Override
    public boolean exists(Long id) {
        return this.internalMap.containsKey(id);
    }
}

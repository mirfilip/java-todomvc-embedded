import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class InMemoryRepositoryTest {

    private InMemoryRepository populatedRepo;
    private InMemoryRepository emptyRepo;

    @Before
    public void setUp() {
        ArrayList<Todo> list = new ArrayList<>();
        list.add(new Todo(1L, "test1"));
        list.add(new Todo(2L, "test2"));

        this.populatedRepo = new InMemoryRepository(list);
        this.emptyRepo = new InMemoryRepository();
    }

    @Test
    public void countReturnsZeroForEmptyRepo() {
        assertEquals(0, emptyRepo.count());
    }

    @Test
    public void countReturnsNonZeroValueForPopulatedRepo() {
        assertEquals(2, populatedRepo.count());
    }

    @Test
    public void findAllReturnsAnEmptyListWhenRepoHasNoTodos() {
        Collection<Todo> result = emptyRepo.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllReturnsTodosAsListInReversedOrder() {
        List<Todo> result = populatedRepo.findAll();

        assertEquals(2, result.size());

        List<Todo> expected = Arrays.asList(
            new Todo(2L, "test2"),
            new Todo(1L, "test1")
        );

        assertEquals(expected, result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void findAllReturnsAnImmutableList() {
        List<Todo> result = populatedRepo.findAll();

        result.add(new Todo(3L, "test3"));
    }

    @Test
    public void findOneReturnsNullWhenRepositoryDoesNotHaveTodo() {
        assertNull(populatedRepo.findOne(5L));
    }

    @Test
    public void findOneReturnsTodoWhenItIsFound() {
        Todo expected = new Todo(2L, "test2");
        assertEquals(expected, populatedRepo.findOne(2L));
    }

    @Test
    public void saveAddsTodo() {
        Todo newItem = new Todo(3L, "test3");

        assertEquals(0, emptyRepo.count());
        assertEquals(newItem, emptyRepo.save(newItem));
        assertEquals(1, emptyRepo.count());
    }

    @Test
    public void saveOverwritesTodoWhenSomethingAlreadyExistsUnderSuchKey() {
        assertEquals(new Todo(2L, "test2"), populatedRepo.findOne(2L));

        Todo itemToOverwrite = new Todo(2L, "another todos");
        assertEquals(itemToOverwrite, populatedRepo.save(itemToOverwrite));

        assertEquals(itemToOverwrite, populatedRepo.findOne(2L));
    }

    @Test
    public void deleteRemovesTodoAndReturnsNumberOfDeletedTodos() {
        assertEquals(new Todo(1L, "test1"), populatedRepo.findOne(1L));
        assertEquals(1, populatedRepo.delete(1L));
        assertNull(populatedRepo.findOne(1L));
    }

    @Test
    public void deleteReturnsZeroWhenRemovingNotExistingTodos() {
        assertEquals(0, populatedRepo.delete(5L));
    }

    @Test
    public void deleteAllClearsTheRepository() {
        populatedRepo.deleteAll();
        assertEquals(0, populatedRepo.count());
    }

    @Test
    public void existsReturnsTrueIfTodoExists() {
        assertTrue(populatedRepo.exists(populatedRepo.findOne(1L).getId()));
    }

    @Test
    public void existsReturnsFalseIfTodoDoesNotExist() {
        assertFalse(populatedRepo.exists(5L));
    }

    @Test
    public void deleteWithPredicate() {
        assertEquals(populatedRepo.count(), populatedRepo.deleteBy(p -> p.getStatus() == Todo.Status.ACTIVE));

        populatedRepo.save(new Todo(5L, "Completed 5", Todo.Status.COMPLETED));
        populatedRepo.save(new Todo(6L, "Completed 6", Todo.Status.COMPLETED));

        assertEquals(2, populatedRepo.deleteBy(p -> p.getStatus() == Todo.Status.COMPLETED));
    }

    @Test
    public void deleteWithCollection() {
        Collection<Todo> todosToDelete = new ArrayList<>(populatedRepo.findAll());

        assertEquals(2, populatedRepo.delete(todosToDelete));
    }

    @Test
    public void findByPredicate() {
        populatedRepo.save(new Todo(5L, "Completed 5", Todo.Status.COMPLETED));
        populatedRepo.save(new Todo(6L, "Active 6", Todo.Status.ACTIVE));

        assertEquals(1, populatedRepo.findAllByStatus(entry -> entry.getValue().getStatus() == Todo.Status.COMPLETED).size());
        assertEquals(3, populatedRepo.findAllByStatus(entry -> entry.getValue().getStatus() == Todo.Status.ACTIVE).size());
    }
}
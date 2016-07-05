import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TodoMVCServletTest extends Mockito {

    @Test
    public void indexSetsAllAndCompletedTodos() throws ServletException, IOException {
        TodoMVCServlet servlet = this.prepareServlet();

        HttpServletRequest request = this.prepareRequestFor("index.do");
        this.prepareRequestDispatcherFor(request);
        HttpServletResponse response = this.prepareResponse();

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("todos"), anyCollectionOf(Todo.class));
        verify(request).setAttribute(eq("completed"), anyCollectionOf(Todo.class));
    }

    @Test
    public void indexIsAlwaysDispatchedAndForwardedToProperJspView() throws ServletException, IOException {
        TodoMVCServlet servlet = this.prepareServlet();

        HttpServletRequest request = this.prepareRequestFor("index.do");
        RequestDispatcher dispatcher = this.prepareRequestDispatcherFor(request);
        HttpServletResponse response = this.prepareResponse();

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/index.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void handleCallIsDelegatedToFrontController() throws ServletException, IOException {
        FrontController controller = mock(FrontController.class);

        TodoMVCServlet servlet = this.prepareServlet(controller);

        HttpServletRequest request = this.prepareRequestFor("index.do");
        this.prepareRequestDispatcherFor(request);
        HttpServletResponse response = this.prepareResponse();

        servlet.doGet(request, response);

        verify(controller, only()).handle(eq("index.do"), anyMapOf(String.class, String[].class));
    }

    private TodoMVCServlet prepareServlet() throws ServletException {
        return this.prepareServlet(null);
    }

    private TodoMVCServlet prepareServlet(FrontController controller) throws ServletException {
        TodoMVCServlet servlet = null !=controller ? new TodoMVCServlet(controller): new TodoMVCServlet();

        ServletConfig config = mock(ServletConfig.class);
        ServletContext context = mock(ServletContext.class);
        when(config.getServletContext()).thenReturn(context);

        servlet.init(config);

        return servlet;
    }

    private HttpServletRequest prepareRequestFor(String endpointUrl) {
        HttpServletRequest request = spy(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn(endpointUrl);

        return request;
    }

    private HttpServletResponse prepareResponse() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted()).thenReturn(false);

        return response;
    }

    private RequestDispatcher prepareRequestDispatcherFor(HttpServletRequest req) {
        RequestDispatcher dispatcher = spy(RequestDispatcher.class);
        when(req.getRequestDispatcher(contains(".jsp"))).thenReturn(dispatcher);

        return dispatcher;
    }
}
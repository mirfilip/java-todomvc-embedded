import java.io.IOException;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        urlPatterns = { "*.do" }
)
public class TodoMVCServlet extends HttpServlet {
    private final FrontController controller;

    public TodoMVCServlet(FrontController controller) {
        this.controller = controller;
    }

    public TodoMVCServlet() {
        this.controller = new FrontController();
        System.out.println("Created a FrontController");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
        forwardToView("index", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);
        /**
         * PRG pattern
         */
        resp.sendRedirect("index.do");
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log("Handling request: " + req.getMethod() + "  " + req.getRequestURI());

        Map<String, Collection<Todo>> attributesToAdd = controller.handle(req.getRequestURI(), req.getParameterMap());
        for (Map.Entry<String, Collection<Todo>> entry : attributesToAdd.entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    void forwardToView(String view, HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        if (!resp.isCommitted()) {
            String path = "/" + view + ".jsp";
            RequestDispatcher dispatcher = req.getRequestDispatcher(path);
            dispatcher.forward(req, resp);
        }
    }
}
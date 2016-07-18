import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer; // TODO: Is this good?

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;

public final class JdkHttpServer implements Server {

    private Application application;
    private final HttpServer server;

    public JdkHttpServer(Application application) throws IOException {
        this.application = application;

        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new Handler())
                .getFilters().add(new JdkHttpServerParamsFilter());

        server.setExecutor(null);
    }

    private class Handler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String url = httpExchange.getRequestURI().toString();

            Map<String, String> parameters = (Map<String, String>) httpExchange.getAttribute("parameters");

            Map<String, Collection<Todo>> response = null;
            String stringResponse = "";

            try {
                response = application.handle(method, url, parameters);

                if (method.equalsIgnoreCase("POST")) {
                    httpExchange.getResponseHeaders().set("Location", "/");
                    httpExchange.sendResponseHeaders(303, -1);
                } else {
                    stringResponse = response.toString();

                    httpExchange.sendResponseHeaders(200, stringResponse.getBytes().length);
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(stringResponse.getBytes());
                    os.close();
                }
            } catch (Exception e) {
                stringResponse = e.getMessage();
                e.printStackTrace();

                httpExchange.sendResponseHeaders(500, stringResponse.getBytes().length);
            }
        }
    }

    @Override
    public Application getApplication() {
        return this.application;
    }

    @Override
    public void run() {
        server.start();
    }
}

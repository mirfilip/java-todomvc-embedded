import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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

            Map<String, String[]> parameters = (Map<String, String[]>) httpExchange.getAttribute("parameters");
            System.out.println(parameters.toString());

            Map<String, Collection<Todo>> response = application.handle(url, parameters);
            String stringResponse = response.toString();
            httpExchange.sendResponseHeaders(200, stringResponse.length());

            OutputStream os = httpExchange.getResponseBody();
            os.write(stringResponse.getBytes());
            os.close();
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

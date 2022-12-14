import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SearchServer {
    private final int PORT = 8989;
    private static Gson GSON = new Gson();
    private BooleanSearchEngine engine;

    public SearchServer(BooleanSearchEngine engine) {
        this.engine = engine;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started");

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    String[] request = in.readLine().split("(?U)\\W+");

                    List<PageEntry> response = engine.search(request);
                    System.out.println(response);

                    out.println(GSON.toJson(response));
                }
            }
        }
    }
}

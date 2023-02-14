import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"), 7_000);
        System.out.println("Indexing completed");
        SearchServer server = new SearchServer(engine);
        server.start();
    }
}
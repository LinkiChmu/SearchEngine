import java.util.List;

public interface SearchEngine {
    List<PageEntry> search(String[] words);
}

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private int size = 7_000;
    private static final int HASH_FACTOR = 2;
    private Map<String, List<PageEntry>> indexingResult = new HashMap<>(size * HASH_FACTOR);
    private Set<String> stopWords;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        if (pdfsDir.isDirectory()) {
            for (File pdf : pdfsDir.listFiles()) {
                var pdfName = pdf.getName();

                try (var doc = new PdfDocument(new PdfReader(pdf))) {
                    for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                        var words = PdfTextExtractor.getTextFromPage(doc.getPage(i)).split("(?U)\\W+");
                        var freqs = countWordsFrequencyOnPage(words);
                        for (var entry : freqs.entrySet()) {
                            recordWordToIndexing(entry, pdfName, i);
                        }
                    }
                }
            }
        }
        loadStopWords("stop-ru.txt");
    }

    private Map<String, Integer> countWordsFrequencyOnPage(String[] words) {
        Map<String, Integer> freqs = new HashMap<>(words.length * HASH_FACTOR);
        for (var word : words) {
            if (word.isEmpty()) {
                continue;
            }
            word = word.toLowerCase();
            freqs.put(word, freqs.getOrDefault(word, 0) + 1);
        }
        return freqs;
    }

    private void recordWordToIndexing(Map.Entry<String, Integer> entry, String pdfName, int page) {
        String keyWord = entry.getKey();
        var singleSearchResult = indexingResult.getOrDefault(
                keyWord, new ArrayList<>());
        singleSearchResult.add(new PageEntry(
                pdfName, page, entry.getValue()));
        indexingResult.put(keyWord, singleSearchResult);
    }

    private void loadStopWords(String textFile) throws IOException {
        var stopWordsAsList = Files.readAllLines(Path.of(textFile));
        stopWords = new HashSet<>(stopWordsAsList);
    }

    @Override
    public List<PageEntry> search(String[] words) {
        Map<PageEntry, Integer> map = new HashMap<>();
        for (var word : words) {
            word = word.toLowerCase();
            if (!stopWords.contains(word)) {
                var rowResult = indexingResult.getOrDefault(word, Collections.emptyList());
                for (var entry : rowResult) {
                    var newValue = map.getOrDefault(entry, 0) + entry.getCount();
                    map.remove(entry);
                    entry.setCount(newValue);
                    map.put(entry, newValue);
                }
            }
        }
        var response = new ArrayList<>(map.keySet());
        response.sort(PageEntry::compareTo);
        return response;
}
}

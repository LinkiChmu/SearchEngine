import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private int size = 7_000;
    private static final int HASH_FACTOR = 2;
    private Map<String, List<PageEntry>> indexingResult = new HashMap<>(size * HASH_FACTOR);

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        if (pdfsDir.isDirectory()) {
            File[] files = pdfsDir.listFiles();
            for (var pdf : files) {
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

    @Override
    public List<PageEntry> search(String word) {
        var response = indexingResult.getOrDefault(word, Collections.emptyList());
        response.sort(PageEntry::compareTo);
        return response;
    }
}

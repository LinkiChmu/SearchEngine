import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> indexation = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        if (pdfsDir.isDirectory()) {
            File[] files = pdfsDir.listFiles();
            for (var pdf : files) {
                var fileName = pdf.getName();

                try (var doc = new PdfDocument(new PdfReader(pdf))) {
                    for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                        var words = PdfTextExtractor.getTextFromPage(doc.getPage(i)).split("(?U)\\W+");

                        var freqs = countWordsFrequencyOnPage(words);

                        for (var entry : freqs.entrySet()) {
                            String keyWord = entry.getKey();
                            var response = indexation.getOrDefault(
                                    keyWord, new ArrayList<>());
                            response.add(new PageEntry(
                                    fileName, i, entry.getValue()));
                            indexation.put(keyWord, response);
                        }
                    }
                }
            }
        }
    }

    public Map<String, Integer> countWordsFrequencyOnPage(String[] words) {
        Map<String, Integer> freqs = new HashMap<>(words.length);
        for (var word : words) {
            if (word.isEmpty()) {
                continue;
            }
            word = word.toLowerCase();
            freqs.put(word, freqs.getOrDefault(word, 0) + 1);
        }
        return freqs;
    }

    @Override
    public List<PageEntry> search(String word) {
        var response = indexation.getOrDefault(word, Collections.emptyList());
        response.sort(PageEntry::compareTo);
        return response;
    }
}

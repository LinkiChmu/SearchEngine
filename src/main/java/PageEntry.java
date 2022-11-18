public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public int compareTo(PageEntry o) {
        if (this.count == o.count) {
            return 0;
        } else if (this.count < o.count) {
            return 1;
        } else {
            return -1;
        }
    }

    public String getPdf() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "PageEntry{pdf=" + pdfName + ", page=" + page + ", count=" + count + '}';
    }
}

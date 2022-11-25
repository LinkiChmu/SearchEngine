import java.util.Objects;

public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    // Method implements comparison for sorting in descending order
    public int compareTo(PageEntry o) {
        if (this.count == o.count) {
            return 0;
        } else if (this.count < o.count) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(pdfName, page);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        PageEntry p = (PageEntry) obj;
        return p.page == this.page && this.pdfName.equals(p.pdfName);
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

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "PageEntry{pdf=" + pdfName + ", page=" + page + ", count=" + count + '}';
    }
}

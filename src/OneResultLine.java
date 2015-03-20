/**
 * Created by minchen on 15/3/7.
 */
public class OneResultLine {
    private String fileName;
    private int frequency;
    private int firstIndex;

    public OneResultLine(String fileName, int index) {
        this.fileName = fileName;
        this.frequency = 1;
        this.firstIndex = index;
    }

    public boolean has(String fileName) {
        return this.fileName.equals(fileName);
    }

    public void addOne(int index) {
        this.frequency++;
        if (index < firstIndex) firstIndex = index;
    }

    public String toString() {
        return "\"" + fileName + "\", " + frequency + ", " + firstIndex;
    }

    public String toWebOutPut() {
        return "<div style=\"margin-top:8px\" >\n<div>\n<a href=\"goto?url=" + fileName + "\">" + fileName + "</a>\n</div>\n<div>\n Frequency: " + frequency + ", First Index: " + firstIndex + "</div>\n</div>\n";
    }

    public int getFrequency() {
        return frequency;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public String getFileName() {
        return fileName;
    }
}

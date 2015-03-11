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

    public void addOne() {
        this.frequency++;
    }

    public String toString() {
        return "\"" + fileName + "\", " + frequency + ", " + firstIndex;
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

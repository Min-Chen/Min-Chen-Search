import java.util.Comparator;

public class QueryResultComparator implements Comparator<OneResultLine> {
    public int compare(OneResultLine l1, OneResultLine l2) {
        if (Integer.compare(l1.getFrequency(), l2.getFrequency()) != 0) return Integer.compare(l1.getFrequency(), l2.getFrequency()) * -1;
        else if(Integer.compare(l1.getFirstIndex(), l2.getFirstIndex()) != 0) return Integer.compare(l1.getFirstIndex(), l2.getFirstIndex()) * -1;
        else return l1.getFileName().compareToIgnoreCase(l2.getFileName());
    }

}

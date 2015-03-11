import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by minchen on 15/3/7.
 */
public class OneQuery {
    private String query;
    private String[] words;
    private List<OneResultLine> result;

    public OneQuery(String newQuery) {
        query = newQuery.trim().toLowerCase();
        String pattern = "[^a-zA-Z0-9]+";
        query = query.replaceAll(pattern, " ");
        words = query.split(" ");

        result = new ArrayList<OneResultLine>();
    }

    public boolean has(String word) {
        for (int i=0; i<words.length; i++) {
            if (word.startsWith(words[i])) return true;
        }
        return false;
    }

    public void add(String fileName, int index) {
        boolean alreadyExist = false;
        int resultIndex = 0;
        for (int i=0; i<result.size(); i++) {
            if (result.get(i).has(fileName)) {
                alreadyExist = true;
                resultIndex = i;
                break;
            }
        }

        if (alreadyExist) {
            result.get(resultIndex).addOne();
        }
        else {
            result.add(new OneResultLine(fileName, index));
        }
    }

    public String toString() {
        String str = query;
        if (result.size() == 0) return str;
        else {
            Collections.sort(result, new QueryResultComparator());
            for (OneResultLine l: result) {
                str += "\n" + l.toString();
            }
        }
        return str;
    }
}

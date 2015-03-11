import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by minchen on 15/3/11.
 */
public class SearchQuery {
    private static List<OneQuery> querys = new ArrayList<OneQuery>();
    private static HashMap<String, HashMap<String, ArrayList<Integer>>> map = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
    private static String pathOutQuery;

    public SearchQuery(HashMap<String, HashMap<String, ArrayList<Integer>>> map, String pathInQuery, String pathOutQuery) {
        this.map = map;
        this.pathOutQuery = pathOutQuery;
        getQuerys(pathInQuery);
        search();
    }

    public static void getQuerys(String path) {
        try {
            FileReader fr = new FileReader (path);
            BufferedReader br = new BufferedReader(fr);

            String str;
            while ((str= br.readLine()) != null){
                querys.add(new OneQuery(str));
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println ("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void search() {
        for (String word: map.keySet()) {
            for (OneQuery q: querys) {
                if (q.has(word)) {
                    HashMap<String, ArrayList<Integer>> fileNamesMap = map.get(word);
                    for (String fileName: fileNamesMap.keySet()) {
                        ArrayList<Integer> indexs = fileNamesMap.get(fileName);
                        for(int i: indexs) {
                            q.add(fileName, i);
                        }
                    }
                }
            }
        }
    }

    public static void outPutQuerys() throws IOException {
        File file = new File(pathOutQuery);
        file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        boolean firstFlag = true;

        for (OneQuery q: querys) {
            if (firstFlag) {
                firstFlag = false;
            }
            else out.write("\n");

            out.write(q + "\n");
        }

        out.flush();
        out.close();
    }
}

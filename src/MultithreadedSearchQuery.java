import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

//This file follows https://github.com/cs212/lectures/blob/fall2014/Advanced%20Multithreading/src/MultithreadedDirectorySizeCalculator.java

/**
 * Created by minchen on 15/3/11.
 */
public class MultithreadedSearchQuery {
    private final WorkQueue minions;
    private int pending;
    private HashMap<String, HashMap<String, ArrayList<Integer>>> map;
    private ArrayList<OneQuery> querys;

    public MultithreadedSearchQuery(HashMap<String, HashMap<String, ArrayList<Integer>>> map, String path) {
        minions = new WorkQueue();
        pending = 0;
        this.map = map;
        querys = new ArrayList<>();

        getQuerys(path);
    }

    public void getQuerys(String path) {
        try {
            FileReader fr = new FileReader (path);
            BufferedReader br = new BufferedReader(fr);

            String str;
            while ((str= br.readLine()) != null){
                minions.execute(new SearchQuery(new OneQuery(str)));
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println ("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outPutQuerys(String path) throws IOException {
        File file = new File(path);
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

    public synchronized void reset() {
        finish();
        querys = new ArrayList<>();
    }

    public synchronized void finish() {
        try {
            while (pending > 0) {
                this.wait();
            }
        }
        catch (InterruptedException e) {
        }
    }

    public synchronized void shutdown() {
        finish();
        minions.shutdown();
    }

    private synchronized void incrementPending() {
        pending++;
    }

    private synchronized void decrementPending() {
        pending--;

        if (pending <= 0) {
            this.notifyAll();
        }
    }

    private synchronized void storeQueryResult(OneQuery q) {
        querys.add(q);
    }

    private class SearchQuery implements Runnable {
        private OneQuery q;

        public SearchQuery(OneQuery q) {
            this.q = q;

            incrementPending();
        }

        public void search() {
            for (String word: map.keySet()) {
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

        @Override
        public void run() {
            search();
            storeQueryResult(q);
            decrementPending();
        }
    }
}

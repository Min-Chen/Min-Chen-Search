import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

//This file follows https://github.com/cs212/lectures/blob/fall2014/Advanced%20Multithreading/src/MultithreadedDirectorySizeCalculator.java

public class MultithreadedSearchQuery {
    private final WorkQueue minions;
    private int pending;
    private final HashMap<String, HashMap<String, ArrayList<Integer>>> map;
    private HashMap<Integer,OneQuery> querys;
    private int queryAmount;

    public MultithreadedSearchQuery(HashMap<String, HashMap<String, ArrayList<Integer>>> map, String path, int threads) {
        minions = new WorkQueue(threads);
        pending = 0;
        this.map = map;
        querys = new HashMap<>();

        getQuerys(path);
    }

    public MultithreadedSearchQuery(HashMap<String, HashMap<String, ArrayList<Integer>>> map, int threads, String query) {
        minions = new WorkQueue(threads);
        pending = 0;
        this.map = map;
        querys = new HashMap<>();

        minions.execute(new SearchQuery(new OneQuery(query), 1));
        queryAmount = 1;
    }

    public boolean finished() {
        return pending == 0;
    }

    public void getQuerys(String path) {
        try {
            FileReader fr = new FileReader (path);
            BufferedReader br = new BufferedReader(fr);

            String str;
            int i = 0;
            while ((str= br.readLine()) != null){
                minions.execute(new SearchQuery(new OneQuery(str), i));
                i++;
            }
            queryAmount = i;

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

        for (int i=0; i<queryAmount; i++) {
            if (i!=0) out.write("\n");
            out.write(querys.get(i) + "\n");
            out.flush();
        }
        out.close();
    }

    public String toWebOutPut() {
        return querys.get(1).toWebOutPut();
    }

    public synchronized void reset() {
        finish();
        querys = new HashMap<>();
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

    private synchronized void storeQueryResult(OneQuery q, int index) {
        querys.put(index, q);
    }

    private class SearchQuery implements Runnable {
        private OneQuery q;
        private int index;

        public SearchQuery(OneQuery q, int index) {
            this.q = q;
            this.index = index;

            incrementPending();
        }

        public void search() {
            for (String word: map.keySet()) {
                if (q.has(word)) {
                    HashMap<String, ArrayList<Integer>> fileNamesMap = map.get(word);
                    for (String fileName: fileNamesMap.keySet()) {
                        ArrayList<Integer> indexs = fileNamesMap.get(fileName);
                        for(int i=0; i<indexs.size(); i++) {
                            q.add(fileName, indexs.get(i));
                        }
                    }
                }
            }
        }

        @Override
        public void run() {
            search();
            storeQueryResult(q,index);
            decrementPending();
        }
    }
}

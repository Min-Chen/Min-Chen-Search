import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//This file follows https://github.com/cs212/lectures/blob/fall2014/Advanced%20Multithreading/src/MultithreadedDirectorySizeCalculator.java

/**
 * Created by minchen on 15/3/11.
 */
public class MultithreadedInvertedIndex {
    private final WorkQueue minions;
    private int pending;
    private HashMap<String, HashMap<String, ArrayList<Integer>>> wholeMap;

    public MultithreadedInvertedIndex(String path) {
        minions = new WorkQueue();
        pending = 0;
        wholeMap = new HashMap<>();

        getFiles(path);
    }

    public synchronized void reset() {
        finish();
        wholeMap = new HashMap<>();
    }

    public synchronized HashMap<String, HashMap<String, ArrayList<Integer>>> getWholeMap() {
        return wholeMap;
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

    public void getFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if(!files[i].isDirectory()){
                if(files[i].toString().toLowerCase().endsWith(".txt")) {
                    minions.execute(new InvertedIndex(files[i].getPath()));
                }
            }
            else getFiles(files[i].getPath());
        }
    }

    public void outPutIndex(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        boolean firstFlag = true;
        Object[] key_arr = wholeMap.keySet().toArray();
        Arrays.sort(key_arr);
        for (Object key : key_arr) {
            if (firstFlag) {
                out.write(key.toString());
                firstFlag = false;
            }
            else out.write("\n\n" + key);
            HashMap map2 = wholeMap.get(key);

            Object[] key_arr2 = map2.keySet().toArray();
            Arrays.sort(key_arr2);
            for (Object key2 : key_arr2) {
                out.write("\n\"" + key2 + "\"");
                ArrayList value2 = (ArrayList) map2.get(key2);

                for (int i=0; i<value2.size(); i++) {
                    out.write(", " + value2.get(i));
                }
            }
        }
        out.flush();
        out.close();
    }

    private synchronized void storeMapToWholeMap(HashMap<String, HashMap<String, ArrayList<Integer>>> map) {
        for (String word: map.keySet()) {
            if (wholeMap.containsKey(word)) {
                for (String fileName: map.get(word).keySet()) {
                    wholeMap.get(word).put(fileName,map.get(word).get(fileName));
                }
            }
            else {
                wholeMap.put(word,map.get(word));
            }
        }
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

    private class InvertedIndex implements Runnable {
        private HashMap<String, HashMap<String, ArrayList<Integer>>> map;
        private String currentFileName;
        private int currentWordIndex;

        public InvertedIndex(String path) {
            this.map = new HashMap<>();
            currentWordIndex = 1;
            currentFileName = new File(path).getAbsolutePath();

            incrementPending();
        }

        private void handleFile() {
            try {
                FileReader fr = new FileReader (currentFileName);
                BufferedReader br = new BufferedReader(fr);

                currentWordIndex = 1;

                String str;
                while ((str= br.readLine()) != null){
                    handleWords(filteredLine(str));
                }
                br.close();
            } catch (FileNotFoundException e) {
                System.out.println ("File not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String filteredLine(String str){
            String pattern = "[^a-zA-Z0-9]+";
            return str.replaceAll(pattern," ");
        }

        private void handleWords(String str) {
            String[] words = str.split(" ");
            for (int i=0; i<words.length; i++) {
                String word = words[i].toLowerCase().trim();
                if (word.equals("")) continue;
                storeWord(word);
                currentWordIndex++;
            }
        }

        private void storeWord(String str) {
            if(map.containsKey(str)) {

                if(map.get(str).containsKey(currentFileName)) {
                    map.get(str).get(currentFileName).add(currentWordIndex);
                }
                else {
                    ArrayList<Integer> temAL = new ArrayList<Integer>();
                    temAL.add(currentWordIndex);
                    map.get(str).put(currentFileName,temAL);
                }
            }
            else {
                ArrayList<Integer> temAL = new ArrayList<Integer>();
                temAL.add(currentWordIndex);
                HashMap<String,ArrayList<Integer>> temMap = new HashMap<String, ArrayList<Integer>>();
                temMap.put(currentFileName, temAL);
                map.put(str, temMap);
            }
        }

        @Override
        public void run() {
            handleFile();
            storeMapToWholeMap(map);
            decrementPending();
        }
    }
}
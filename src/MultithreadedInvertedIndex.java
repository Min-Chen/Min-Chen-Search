import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This file follows https://github.com/cs212/lectures/blob/fall2014/Advanced%20Multithreading/src/MultithreadedDirectorySizeCalculator.java

public class MultithreadedInvertedIndex {
    private final WorkQueue minions;
    private int pending;
    private HashMap<String, HashMap<String, ArrayList<Integer>>> wholeMap;
    private boolean isWriting;
    private String seed;
    private Set<String> pages;

    public MultithreadedInvertedIndex(int threads, String seed) {
        minions = new WorkQueue(threads);
        pending = 0;
        wholeMap = new HashMap<>();
        isWriting = false;
        this.seed = seed;
        pages = new HashSet<>();

        searchNewPage(seed);
    }

    public MultithreadedInvertedIndex(String path, int threads) {
        minions = new WorkQueue(threads);
        pending = 0;
        wholeMap = new HashMap<>();
        isWriting = false;
        seed = null;

        getFiles(path);
    }

    private  synchronized boolean searchNewPage(String url) {
        if (pages.size() == 50) return false;
        if (pages.contains(url)) return true;
        pages.add(url);
        minions.execute(new InvertedIndexForWeb(url));
        return true;
    }

    public boolean finished() {
        return pending == 0;
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
            out.flush();
        }
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
            currentFileName = new File(path).toString();

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

    // The fetch HTML content part comes from https://github.com/cs212/lectures/blob/fall2014/Sockets/src/HTTPFetcher.java
    private class InvertedIndexForWeb implements Runnable {
        private HashMap<String, HashMap<String, ArrayList<Integer>>> map;
        private String url;
        private int currentWordIndex;

        public InvertedIndexForWeb(String url) {
            this.map = new HashMap<>();
            currentWordIndex = 1;
            this.url = url;

            incrementPending();
        }

        private void handlePage() throws IOException {
            currentWordIndex = 1;
            String str = fetchHTML(url);
            for (String s : str.split(System.lineSeparator())) {
                handleWords(filteredLine(s));
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

                if(map.get(str).containsKey(url)) {
                    map.get(str).get(url).add(currentWordIndex);
                }
                else {
                    ArrayList<Integer> temAL = new ArrayList<Integer>();
                    temAL.add(currentWordIndex);
                    map.get(str).put(url,temAL);
                }
            }
            else {
                ArrayList<Integer> temAL = new ArrayList<Integer>();
                temAL.add(currentWordIndex);
                HashMap<String,ArrayList<Integer>> temMap = new HashMap<String, ArrayList<Integer>>();
                temMap.put(url, temAL);
                map.put(str, temMap);
            }
        }

        @Override
        public void run() {
            try {
                handlePage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            storeMapToWholeMap(map);
            decrementPending();
        }

        public List<String> fetchLines(URL url, String request)
                throws UnknownHostException, IOException
        {
            ArrayList<String> lines = new ArrayList<>();

            try (
                    Socket socket = new Socket(url.getHost(), 80);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream());
            ) {
                writer.println(request);
                writer.flush();

                String line = null;

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            return lines;
        }

        public String craftHTTPRequest(URL url) {
            String host = url.getHost();
            String resource = url.getFile().isEmpty() ? "/" : url.getFile();

            // The specification is specific about where to use a new line
            // versus a carriage return!
            return String.format(
                    "%s %s %s\n" +
                            "Host: %s\n" +
                            "Connection: close\n" +
                            "\r\n",
                    "GET", resource, "HTTP/1.1", host);
        }

        public String fetchHTML(String url)
                throws UnknownHostException, MalformedURLException, IOException
        {
            URL target = new URL(url);
            String request = craftHTTPRequest(target);
            List<String> lines = fetchLines(target, request);

            int start = 0;
            int end = lines.size();

            // Determines start of HTML versus headers.
            while (!lines.get(start).trim().isEmpty() && (start < end)) {
                start++;
            }

            // Double-check this is an HTML file.
            Map<String, String> fields = parseHeaders(lines.subList(0, start + 1));
            String type = fields.get("Content-Type");

            if ((type != null) && type.toLowerCase().contains("html")) {
                List<String> stripedLines = new ArrayList<>();
                String tem = String.join(System.lineSeparator(), lines.subList(start + 1,end));
                tem = tem.replaceAll("<(script|SCRIPT)[\\s\\S]*?>[\\s\\S]*?<\\/(script|SCRIPT)>","");  // strip SCRIPT section
                tem = tem.replaceAll("<(style|STYLE)[\\s\\S]*?>[\\s\\S]*?<\\/(style|STYLE)>","");  // strip STYLE section

                for (String s : tem.split(System.lineSeparator())) {

                    if (pages.size() < 50) {
                        //find out links
                        String pattern = "(?<=href=\")[\\s\\S]*?(?=[\"#?])";
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(s.toLowerCase());
                        if (m.find()) {
                            String realUrl = m.group(0);
                            int startIndex = s.toLowerCase().indexOf(realUrl);
                            realUrl = s.substring(startIndex,startIndex + realUrl.length());

                            URL base = new URL(url);
                            URL absolute = new URL(base, realUrl);
                            if (!absolute.toString().endsWith("css") && !absolute.toString().endsWith("js")) {
                                searchNewPage(absolute.toString());
                            }
                        }
                    }

                    //stripe HTML tags
                    String stripedString = s.replaceAll("<[\\s\\S]*?>","");
                    stripedString = stripedString.replaceAll("<\\/[\\s\\S]*?>","");
                    stripedString = stripedString.replaceAll("&[\\s\\S]*?;",""); // stripe &nbsp; and &#8217; and things like that
                    if (stripedString.equals("")) continue;
                    stripedLines.add(stripedString);
                }

                String finalString = String.join(System.lineSeparator(), stripedLines);
                finalString = finalString.replaceAll("<[\\s\\S]*?>","");
                return finalString;
            }

            return null;
        }

        public Map<String, String> parseHeaders(List<String> headers) {
            Map<String, String> fields = new HashMap<>();

            if ((headers.size() > 0) && headers.get(0).startsWith("HTTP/1.1")) {
                fields.put("Status", headers.get(0).substring("HTTP/1.1".length()).trim());

                for (String line : headers.subList(1, headers.size())) {
                    String[] pair = line.split(":", 2);

                    if (pair.length == 2) {
                        fields.put(pair[0].trim(), pair[1].trim());
                    }
                }
            }

            return fields;
        }
    }
}
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This file is copied from https://github.com/cs212/lectures/blob/fall2014/Sockets/src/HTTPFetcher.java

/**
 * An example class designed to make fetching the results of different HTTP
 * operations easier.
 */
public class HTTPFetcher {
    /** Port used by socket. For web servers, should be port 80. */
    public static final int PORT = 80;

    /** Version of HTTP used and supported. */
    public static final String version = "HTTP/1.1";

    /** Valid HTTP method types. */
    public static enum HTTP {
        OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
    };

    /**
     * Will connect to the web server and fetch the URL using the HTTP
     * request provided. It would be more efficient to operate on each
     * line as returned instead of storing the entire result as a list.
     *
     * @param url - url to fetch
     * @param request - full HTTP request
     *
     * @return the lines read from the web server
     *
     * @throws IOException
     * @throws UnknownHostException
     */
    public static List<String> fetchLines(URL url, String request)
            throws UnknownHostException, IOException
    {
        ArrayList<String> lines = new ArrayList<>();

        try (
                Socket socket = new Socket(url.getHost(), PORT);
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

    /**
     * Crafts a minimal HTTP/1.1 request for the provided method.
     *
     * @param url - url to fetch
     * @param type - HTTP method to use
     *
     * @return HTTP/1.1 request
     *
     * @see {@link HTTP}
     */
    public static String craftHTTPRequest(URL url, HTTP type) {
        String host = url.getHost();
        String resource = url.getFile().isEmpty() ? "/" : url.getFile();

        // The specification is specific about where to use a new line
        // versus a carriage return!
        return String.format(
                "%s %s %s\n" +
                        "Host: %s\n" +
                        "Connection: close\n" +
                        "\r\n",
                type.name(), resource, version, host);
    }



    /**
     * Fetches the HTML for the specified URL (without headers).
     *
     * @param url - url to fetch
     * @return HTML as a single {@link String}, or null if not HTML
     *
     * @throws UnknownHostException
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String fetchHTML(String url)
            throws UnknownHostException, MalformedURLException, IOException
    {
        URL target = new URL(url);
        String request = craftHTTPRequest(target, HTTP.GET);
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

            for (String s : lines.subList(start + 1,end)) {
                //find out links
                String pattern = "(?<=href=\")[\\s\\S]*?(?=[\"#?])";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(s.toLowerCase());
                if (m.find()) {
                    URL base = new URL(url);
                    URL absolute = new URL(base, m.group(0));
                    if (!absolute.toString().endsWith("css") && !absolute.toString().endsWith("js")) System.out.println(absolute );
                }

                //stripe HTML tags
                s = s.replaceAll("<[\\s\\S]*?>","");
                s = s.replaceAll("<\\/[\\s\\S]*?>","");
                if (s.equals("")) continue;
                stripedLines.add(s);
            }

            //return String.join(System.lineSeparator(), lines.subList(start + 1, end));
            return String.join(System.lineSeparator(), stripedLines);
        }

        return null;
    }

    /**
     * Helper method that parses HTTP headers into a map where the key is
     * the field name and the value is the field value. The status code
     * will be stored under the key "Status".
     *
     * @param headers - HTTP/1.1 header lines
     * @return field names mapped to values if the headers are properly formatted
     */
    public static Map<String, String> parseHeaders(List<String> headers) {
        Map<String, String> fields = new HashMap<>();

        if ((headers.size() > 0) && headers.get(0).startsWith(version)) {
            fields.put("Status", headers.get(0).substring(version.length()).trim());

            for (String line : headers.subList(1, headers.size())) {
                String[] pair = line.split(":", 2);

                if (pair.length == 2) {
                    fields.put(pair[0].trim(), pair[1].trim());
                }
            }
        }

        return fields;
    }

    public static void main(String[] args) throws Exception {
        String url = "http://logging.apache.org/log4j/1.2/apidocs/allclasses-noframe.html";
        System.out.println(fetchHTML(url));
    }
}
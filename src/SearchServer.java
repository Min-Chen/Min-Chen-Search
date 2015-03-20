import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

//Origional from https://github.com/cs212/lectures/blob/fall2014/DynamicHTML/src/HelloServer.java

/**
 * Demonstrates the danger of using user-input in a web application, especially
 * regarding cross-site scripting (XSS) attacks.
 */
public class SearchServer {

    public static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(HomePage.class, "/index");
        handler.addServletWithMapping(SearchPage.class, "/search");
        //handler.addServletWithMapping(TodayServer.TodayServlet.class, "/today");

        server.setHandler(handler);
        server.start();
        server.join();
    }

    public static String dayOfWeek() {
        return Calendar.getInstance().getDisplayName(
                Calendar.DAY_OF_WEEK,
                Calendar.LONG,
                Locale.ENGLISH);
    }

    @SuppressWarnings("serial")
    public static class HomePage extends HttpServlet {
        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();
            printHeader(out, "Min Chen Search");

            String name = request.getParameter("name");
            name = name == null ? "anonymous" : name;

            response.setIntHeader("X-XSS-Protection", 0);
            out.printf("<div style=\"margin-top:100px\" >");
            out.printf("<h1>Min Chen Search</h1>%n");

            out.printf("<form action=\"search\" method=\"get\">");
            out.printf("<input type=\"text\" style = \"height:32px;width:600px;padding:3px;font-size:16px;\" name = \"query\"/>");
            out.printf("<input type=\"submit\" value=\"Submit\" style = \"height:32px;width:60px;padding:3px;margin-left:20px;line-height:30px;font-size:24px\"/>");
            out.printf("</form>");

            out.printf("</div>");

            out.printf("</body>%n");
            out.printf("</html>%n");
        }
    }

    public static class SearchPage extends HttpServlet {
        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();
            printHeader(out, "Min Chen Search");

            String name = request.getParameter("name");
            name = name == null ? "anonymous" : name;

            response.setIntHeader("X-XSS-Protection", 0);
            out.printf("<div style=\"margin-top:100px\" >");
            out.printf("<h1>Min Chen Search</h1>%n");

            out.printf("<form action=\"search\" method=\"get\">");
            out.printf("<input type=\"text\" style = \"height:32px;width:600px;padding:3px;font-size:16px;\" />");
            out.printf("<input type=\"submit\" value=\"Submit\" style = \"height:32px;width:60px;padding:3px;margin-left:20px;line-height:30px;font-size:24px\"/>");
            out.printf("</form>");

            out.printf("</div>");

            out.printf("</body>%n");
            out.printf("</html>%n");
        }
    }

    private static void printHeader(PrintWriter out, String title) {
        out.printf("<html>%n");
        out.printf("<head><title>%s</title></head>%n", title);
        out.printf("<body style=\"width:960px;margin-left:auto;margin-right:auto\" >");
        out.printf("<div style = \"height:25px\" >");
        out.printf("<span style=\"float:left;\">");
        out.printf("<a href = \"SearchHistory\" style=\"margin-right:15px\">Search History</a>");
        out.printf("<a href = \"VisitHistory\" style=\"margin-right:15px\">Visit History</a>");
        out.printf("<a href = \"FavoriteResult\" style=\"margin-right:15px\">Favorite Result</a>");
        out.printf("</span>%n");

        out.printf("<span style=\"float:right\">");
        out.printf("<a href = \"Register\" style=\"margin-right:15px\">Register</a>");
        out.printf("<a href = \"SignIn\" style=\"margin-right:15px\">Sign In</a>");
        out.printf("</span>");

        out.printf("</div>");
    }
}
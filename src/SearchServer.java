import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import static java.lang.Thread.sleep;

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
        handler.addServletWithMapping(RegisterPage.class, "/Register");
        handler.addServletWithMapping(Register2Page.class, "/Register2");
        handler.addServletWithMapping(LoginPage.class, "/Login");
        handler.addServletWithMapping(Login2Page.class, "/Login2");
        handler.addServletWithMapping(LogoutPage.class, "/Logout");
        handler.addServletWithMapping(ChangePwdPage.class, "/ChangePwd");
        handler.addServletWithMapping(ChangePwd2Page.class, "/ChangePwd2");

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
            printHeader(out, "Min Chen Search", request);

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
            printHeader(out, "Min Chen Search", request);

            String name = request.getParameter("name");
            name = name == null ? "anonymous" : name;



            response.setIntHeader("X-XSS-Protection", 0);
            out.printf("<div style=\"margin-top:100px\" >");
            out.printf("<h1>Min Chen Search</h1>%n");

            out.printf("<form action=\"search\" method=\"get\">");
            out.printf("<input type=\"text\" style = \"height:32px;width:600px;padding:3px;font-size:16px;\" name = \"query\"/>");
            out.printf("<input type=\"submit\" value=\"Submit\" style = \"height:32px;width:60px;padding:3px;margin-left:20px;line-height:30px;font-size:24px\"/>");
            out.printf("</form>");

            String query = request.getParameter("query");
            out.printf("<div style = \"font-size:18px\" >Search results for : %s</div>", query);

            MultithreadedInvertedIndex ii = new MultithreadedInvertedIndex(5,"http://logging.apache.org/log4j/1.2/apidocs/allclasses-noframe.html");
            try {
                while (true) {
                    sleep(10);
                    if (ii.finished()) break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            MultithreadedSearchQuery sq = new MultithreadedSearchQuery(ii.getWholeMap(), 5, query);
            try {
                while (true) {
                    sleep(10);
                    if (sq.finished()) break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.printf(sq.toWebOutPut());

            out.printf("</div>");

            out.printf("</body>%n");
            out.printf("</html>%n");
        }
    }

    public static class RegisterPage extends HttpServlet {
        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();

            response.setIntHeader("X-XSS-Protection", 0);

            printHeader(out, "Register", request);

            out.printf("<div style=\"margin-top:100px\" >");
            out.printf("<h1>User Register</h1>%n");

            out.printf("<form action=\"Register2\" method=\"get\">");
            out.printf("<h4>User Name</h4>");
            out.printf("<input type=\"text\" style = \"height:26px;width:125px;padding:2px;font-size:16px;\" name = \"name\"/>");
            out.printf("<h4>Password</h4>");
            out.printf("<input type=\"text\" style = \"height:26px;width:125px;padding:2px;font-size:16px;\" name = \"pwd\"/>");
            out.printf("<br><br><input type=\"submit\" value=\"Submit\" style = \"height:32px;width:60px;padding:3px;line-height:30px;font-size:24px\"/>");
            out.printf("</form>");

            out.printf("</div>");

            out.printf("</body>%n");
            out.printf("</html>%n");

        }
    }

    public static class Register2Page extends HttpServlet {
        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();

            response.setIntHeader("X-XSS-Protection", 0);

            out.printf("<div style=\"margin-top:100px\" >");

            String name = request.getParameter("name");
            try {
                ResultSet rs = WebUtil.executeQuery("SELECT COUNT(*) FROM USER WHERE NAME = '" + name + "' ");
                if (rs.next()) {
                    int exist = rs.getInt(1);
                    WebUtil.closeDB();
                    if (exist > 0) {
                        out.printf("<h1>This User Name has already been used, please try another one.</h1>%n");
                        out.printf("<a href=\"Register\" >Try again</a>%n");
                    }
                    else {
                        String pwd = request.getParameter("pwd");
                        WebUtil.executeSQL("INSERT INTO USER(NAME,PWD) VALUES('" + name + "','" + pwd + "')");
                        WebUtil.closeDB();
                        WebUtil.setCookie(response, "name", name, 15 * 24 * 3600);
                        out.printf("<h1>Success!</h1>%n");
                        out.printf("<a href=\"index\" >Home Page</a>%n");
                    }
                }
                WebUtil.closeDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            out.printf("</body>%n");
            out.printf("</html>%n");

        }
    }

    public static class LoginPage extends HttpServlet {
        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();

            response.setIntHeader("X-XSS-Protection", 0);

            printHeader(out, "Login", request);

            out.printf("<div style=\"margin-top:100px\" >");
            out.printf("<h1>Login</h1>%n");

            out.printf("<form action=\"Login2\" method=\"get\">");
            out.printf("<h4>User Name</h4>");
            out.printf("<input type=\"text\" style = \"height:26px;width:125px;padding:2px;font-size:16px;\" name = \"name\"/>");
            out.printf("<h4>Password</h4>");
            out.printf("<input type=\"password\" style = \"height:26px;width:125px;padding:2px;font-size:16px;\" name = \"pwd\"/>");
            out.printf("<br><br><input type=\"submit\" value=\"Submit\" style = \"height:32px;width:60px;padding:3px;line-height:30px;font-size:24px\"/>");
            out.printf("</form>");

            out.printf("</div>");

            out.printf("</body>%n");
            out.printf("</html>%n");

        }
    }

    public static class Login2Page extends HttpServlet {
        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();

            response.setIntHeader("X-XSS-Protection", 0);


            out.printf("<div style=\"margin-top:100px\" >");

            String name = request.getParameter("name");
            String pwd = request.getParameter("pwd");

            try {
                ResultSet rs = WebUtil.executeQuery("SELECT COUNT(*) FROM USER WHERE NAME = '" + name + "' AND PWD = '" + pwd + "' ");
                if (rs.next()) {
                    int exist = rs.getInt(1);
                    WebUtil.closeDB();
                    if (exist > 0) {
                        WebUtil.setCookie(response, "name", name, 15 * 24 * 3600);
                        out.printf("<h1>Login Success!</h1>%n");
                        out.printf("<a href=\"index\" >Home Page</a>%n");
                    }
                    else {
                        out.printf("<h1>User Name or Password wrong, please try again.</h1>%n");
                        out.printf("<a href=\"Login\" >Try again</a>%n");
                    }
                }
                WebUtil.closeDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            out.printf("</body>%n");
            out.printf("</html>%n");

        }
    }

    public static class ChangePwdPage extends HttpServlet {
        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();

            response.setIntHeader("X-XSS-Protection", 0);

            printHeader(out, "Login", request);

            out.printf("<div style=\"margin-top:100px\" >");
            out.printf("<h1>Login</h1>%n");

            out.printf("<form action=\"ChangePwd2\" method=\"get\">");
            out.printf("<h4>Old Password</h4>");
            out.printf("<input type=\"text\" style = \"height:26px;width:125px;padding:2px;font-size:16px;\" name = \"old\"/>");
            out.printf("<h4>New Password</h4>");
            out.printf("<input type=\"text\" style = \"height:26px;width:125px;padding:2px;font-size:16px;\" name = \"new\"/>");
            out.printf("<br><br><input type=\"submit\" value=\"Submit\" style = \"height:32px;width:60px;padding:3px;line-height:30px;font-size:24px\"/>");
            out.printf("</form>");

            out.printf("</div>");

            out.printf("</body>%n");
            out.printf("</html>%n");

        }
    }

    public static class ChangePwd2Page extends HttpServlet {
        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();

            response.setIntHeader("X-XSS-Protection", 0);

            out.printf("<div style=\"margin-top:100px\" >");

            String name = WebUtil.getCookieValue(request, "name");
            String old = request.getParameter("old");
            String newPwd = request.getParameter("new");

            try {
                ResultSet rs = WebUtil.executeQuery("SELECT COUNT(*) FROM USER WHERE NAME = '" + name + "' AND PWD = '" + old + "' ");
                if (rs.next()) {
                    int exist = rs.getInt(1);
                    WebUtil.closeDB();
                    if (exist > 0) {
                        WebUtil.executeSQL("UPDATE USER SET PWD = '" + newPwd + "' ");

                        out.printf("<h1>Success!</h1>%n");
                        out.printf("<a href=\"index\" >Home Page</a>%n");
                    }
                    else {
                        out.printf("<h1>Old Password wrong, please try again.</h1>%n");
                        out.printf("<a href=\"ChangePwd\" >Try again</a>%n");
                    }
                }
                WebUtil.closeDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            out.printf("</body>%n");
            out.printf("</html>%n");

        }
    }

    public static class LogoutPage extends HttpServlet {
        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();

            response.setIntHeader("X-XSS-Protection", 0);

            printHeader(out, "login", request);

            out.printf("<div style=\"margin-top:100px\" >");

            WebUtil.killCookie(response, "name");
            response.sendRedirect("index");
        }
    }

    private static void printHeader(PrintWriter out, String title, HttpServletRequest request) {
        out.printf("<html>%n");
        out.printf("<head><title>%s</title></head>%n", title);
        out.printf("<body style=\"width:960px;margin-left:auto;margin-right:auto\" >");
        out.printf("<div style = \"height:25px\" >");
        out.printf("<span style=\"float:left;\">");
        out.printf("<a href = \"index\" style=\"margin-right:15px\">Home Page</a>");
        out.printf("<a href = \"SearchHistory\" style=\"margin-right:15px\">Search History</a>");
        out.printf("<a href = \"VisitHistory\" style=\"margin-right:15px\">Visit History</a>");
        out.printf("<a href = \"FavoriteResult\" style=\"margin-right:15px\">Favorite Result</a>");
        out.printf("</span>%n");

        out.printf("<span style=\"float:right\">");

        String name = WebUtil.getCookieValue(request,"name");
        if (name == null) {
            out.printf("<a href = \"Register\" style=\"margin-right:15px\">Register</a>");
            out.printf("<a href = \"Login\" style=\"margin-right:15px\">Sign In</a>");
        }
        else {
            out.printf("<span style=\"margin-right:20px;\">Hello, " + name + "</span>");
            out.printf("<a href = \"ChangePwd\" style=\"margin-right:15px\">Change Password</a>");
            out.printf("<a href = \"Logout\" style=\"margin-right:15px\">Log out</a>");
        }
        out.printf("</span>");
        out.printf("</div>");
    }
}
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

public class WebUtil {
    static private Connection c;

    public static Statement connectToDB() throws SQLException {
        c = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
        Statement statement = c.createStatement();
        return statement;
    }

    public static void executeSQL(String sqlString) throws SQLException {
        closeDB();
        Statement s = connectToDB();
        s.executeUpdate(sqlString);
    }

    public static ResultSet executeQuery(String sqlString) throws SQLException {
        closeDB();
        Statement s = connectToDB();
        return s.executeQuery(sqlString);
    }

    public static void closeDB() throws SQLException {
        if (c!=null && !c.isClosed()) c.close();
    }

    public static Cookie getCookie(HttpServletRequest request, String key) {
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(key)) return c;
        }
        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String key)
    {
        Cookie c = getCookie(request, key);
        if ( c!=null ) {
            return c.getValue();
        }
        return null;
    }

    public static void setCookie(HttpServletResponse response, String key, String value, int time)
    {
        Cookie c = new Cookie(key,value);
        c.setMaxAge(time);
        c.setPath("/");
        response.addCookie(c);
    }

    public static void killCookie(HttpServletResponse response, String key) {
        Cookie c = new Cookie(key,"false");
        c.setMaxAge(0);
        c.setPath("/");
        response.addCookie(c);
    }

    public static int getUserIdByName(String name) {
        try {
            ResultSet rs = executeQuery("SELECT ID FROM USER WHERE NAME = '" + name + "' ");
            if (rs.next()) {
                int userId = rs.getInt(1);
                closeDB();
                return userId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

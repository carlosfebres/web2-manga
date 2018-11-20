package Models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.ConnectionMySQL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class User {
    public int user_id;
    public int type_id;
    public String user_username;
    public String user_name;
    public String user_email;
    public String user_creation_time;

    public static User get(int id) {

        PreparedStatement ps = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ObjectMapper objMapper = new ObjectMapper();
        User user = null;

        try {
            ps = ConnectionMySQL.getConnection().prepareStatement("SELECT * FROM users WHERE user_id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setType_id(rs.getInt("type_id"));
                user.setUser_creation_time(rs.getString("user_creation_time"));
                user.setUser_email(rs.getString("user_email"));
                user.setUser_id(rs.getInt("user_id"));
                user.setUser_name(rs.getString("user_name"));
                user.setUser_username(rs.getString("user_username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User fromSession(HttpServletRequest request) {
        HttpSession session = (HttpSession) request.getSession();
        String user = (String) session.getAttribute("user");
        if (user != null) {
            ObjectMapper objMapper = new ObjectMapper();
            try {
                return objMapper.readValue(user, User.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public String getUser_username() {
        return user_username;
    }

    public void setUser_username(String user_username) {
        this.user_username = user_username;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_creation_time() {
        return user_creation_time;
    }

    public void setUser_creation_time(String user_creation_time) {
        this.user_creation_time = user_creation_time;
    }

    public void storeSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(true);
            ObjectMapper objMapper = new ObjectMapper();
            String json = objMapper.writeValueAsString( this );
            session.setAttribute("user", json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

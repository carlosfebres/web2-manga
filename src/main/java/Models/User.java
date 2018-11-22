package Models;

import Interfaces.Model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.ConnectionMySQL;
import utils.Props;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User implements Model {
    public int userId;
    public int typeId;
    public String userUsername;
    public String userName;
    public String userEmail;
    public String userCreationTime;
    private String password;

    public static User get(int id) {

        PreparedStatement ps = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ObjectMapper objMapper = new ObjectMapper();
        User user = null;

        try {
            ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_users"));
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setTypeId(rs.getInt("type_id"));
                user.setUserCreationTime(rs.getString("user_creation_time"));
                user.setUserEmail(rs.getString("user_email"));
                user.setUserId(rs.getInt("user_id"));
                user.setUserName(rs.getString("user_name"));
                user.setUserUsername(rs.getString("user_username"));
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int type_id) {
        this.typeId = type_id;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String user_username) {
        this.userUsername = user_username;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user_name) {
        this.userName = user_name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String user_email) {
        this.userEmail = user_email;
    }

    public String getUserCreationTime() {
        return userCreationTime;
    }

    public void setUserCreationTime(String user_creation_time) {
        this.userCreationTime = user_creation_time;
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

    @Override
    public boolean save() {PreparedStatement ps = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        Response<User> resp = new Response<>();

        try {
            String query = Props.getProperty("query_check");
            pst = ConnectionMySQL.getConnection().prepareStatement(query);
            pst.setString(1, userUsername);
            rs = pst.executeQuery();

            if (!rs.absolute(1)) {

                String insertQuery = Props.getProperty("insert_user");
                ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, this.getUserEmail());
                ps.setString(2, this.getUserName());
                ps.setString(3, this.getPassword());
                ps.setString(4, this.getUserUsername());
                ps.setInt(5, this.getTypeId());
                System.out.println(ps.toString());
                if (ps.executeUpdate() == 1) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            this.setUserId( generatedKeys.getInt(1) );
                            return true;
                        } else {
                            throw new SQLException("Creating user failed, no ID obtained.");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error " + e);

        }
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String getPassword() {
        return password;
    }
}

package Models;

import Exceptions.ModelNotFound;
import Interfaces.Model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import CustomUtils.ConnectionMySQL;
import CustomUtils.Props;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User implements Model {
	private int userId = -1;
	private int typeId;
	private String userUsername;
	private String userName;
	private String userEmail;
	private String userCreationTime;
	private String password;


	public static User get(int id) throws ModelNotFound {
		User user = new User();
		User.fetchAndFill(id, user);
		return user;
	}

	public static User get(String id) throws ModelNotFound {
		return User.get(Integer.parseInt(id));
	}

	public void fetch() throws ModelNotFound {
		User.fetchAndFill(this.getUserId(), this);
	}

	private static void fetchAndFill(int id, Model obj) throws ModelNotFound {
		User user = (User) obj;
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_users"));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				user.setTypeId(rs.getInt("type_id"));
				user.setUserCreationTime(rs.getString("user_creation_time"));
				user.setUserEmail(rs.getString("user_email"));
				user.setUserId(rs.getInt("user_id"));
				user.setUserName(rs.getString("user_name"));
				user.setUserUsername(rs.getString("user_username"));
			} else {
				throw new ModelNotFound("User");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static User fromSession(HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			String user = (String) session.getAttribute("user");
			if (user != null) {
				ObjectMapper objMapper = new ObjectMapper();
				return objMapper.readValue(user, User.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void storeSession(HttpServletRequest request) {
		try {
			HttpSession session = request.getSession(true);
			ObjectMapper objMapper = new ObjectMapper();
			String json = objMapper.writeValueAsString(this);
			session.setAttribute("user", json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public boolean login() {
		try {
			String query = Props.getProperty("sql_autheticate");
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(query);
			ps.setString(1, this.getUserUsername());
			ps.setString(2, this.password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				this.setUserId(rs.getInt("user_id"));
				this.fetch();
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Error " + e);
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean save() {
		PreparedStatement ps;
		PreparedStatement pst;
		ResultSet rs;
		try {
			if (this.getUserId() == -1) {
				String query = Props.getProperty("query_check");
				pst = ConnectionMySQL.getConnection().prepareStatement(query);
				pst.setString(1, userUsername);
				rs = pst.executeQuery();

				if (!rs.absolute(1)) {
					String insertQuery = Props.getProperty("insert_user");
					ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, this.getUserEmail());
					ps.setString(2, this.getUserName());
					ps.setString(3, this.password);
					ps.setString(4, this.getUserUsername());
					ps.setInt(5, this.getTypeId());
					if (ps.executeUpdate() == 1) {
						try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
							if (generatedKeys.next()) {
								this.setUserId(generatedKeys.getInt(1));
								return true;
							} else {
								throw new SQLException("Creating user failed, no ID obtained.");
							}
						}
					}
				}
			} else {
				String updateQuery = Props.getProperty("update_user");
				ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
				ps.setString(1, this.getUserEmail());
				ps.setString(2, this.getUserName());
				ps.setString(3, this.password);
				ps.setString(4, this.getUserUsername());
				ps.setInt(5, this.getTypeId());
				ps.setInt(6, this.getUserId());
				ps.executeUpdate();
				return true;
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

	// -------------------------------------------------


	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getUserUsername() {
		return userUsername;
	}

	public void setUserUsername(String userUsername) {
		this.userUsername = userUsername;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserCreationTime() {
		return userCreationTime;
	}

	public void setUserCreationTime(String userCreationTime) {
		this.userCreationTime = userCreationTime;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

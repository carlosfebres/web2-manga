package utils;

import Models.Response;
import Models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;


public class Queries {


	public User authentication(String user_username, String user_password) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String query = Props.getProperty("query1");
			ps = ConnectionMySQL.getConnection().prepareStatement(query);
			ps.setString(1, user_username);
			ps.setString(2, user_password);
			rs = ps.executeQuery();

			if (rs.absolute(1)) {
				User user = new User();
				user.setTypeId(rs.getInt("type_id"));
				user.setUserCreationTime(rs.getString("user_creation_time"));
				user.setUserEmail(rs.getString("user_email"));
				user.setUserId(rs.getInt("user_id"));
				user.setUserName(rs.getString("user_name"));
				user.setUserUsername(rs.getString("user_username"));
				return user;
			}
		} catch (SQLException e) {
			System.err.println("Error " + e);
		}
		return null;
	}

	public User registry(String user_name, String last_name, String user_email, String user_username, String user_password, int type_id) {

		PreparedStatement ps = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Response<User> resp = new Response<>();


		try {
			String query = Props.getProperty("query_check");
			pst = ConnectionMySQL.getConnection().prepareStatement(query);
			pst.setString(1, user_username);
			rs = pst.executeQuery();

			if (!rs.absolute(1)) {

				String insertQuery = Props.getProperty("query2");
				ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, user_email);
				ps.setString(2, user_name + " " + last_name);
				ps.setString(3, user_password);
				ps.setString(4, user_username);
				ps.setInt(5, type_id);
				System.out.println(ps.toString());
				if (ps.executeUpdate() == 1) {
					try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							return User.get(generatedKeys.getInt(1));
						} else {
							throw new SQLException("Creating user failed, no ID obtained.");
						}
					}
				}
			}

		} catch (SQLException e) {
			System.err.println("Error " + e);

		}
		return null;
	}

	public boolean createManga(int user_id, String manga_genre, String manga_name, int manga_status, String manga_synopsis, String timestamp) {

		PreparedStatement ps = null;

		PreparedStatement pst = null;
		int rs = 0;

		try (Connection connection = ConnectionMySQL.getConnection()) {
			connection.setAutoCommit(false);

			try {
				System.out.println("starting transaction");
				ArrayList<String> queries = new ArrayList<String>();
				queries.add("insert into manga (userId,manga_name,manga_synopsis,manga_status,manga_creation_time) values (?,?,?,?,?)");
				queries.add("select genres_id from genres where genre_des = ?");
				queries.add("insert into manga_genre (genres_id,manga_id) values (?,?)");

				pst = connection.prepareStatement(queries.get(0));
				pst.setInt(1, user_id);
				pst.setString(2, manga_name);
				pst.setString(3, manga_synopsis);
				pst.setInt(4, manga_status);
				pst.setString(5, timestamp);
				System.out.println(pst.toString());
				pst.executeUpdate();
				pst = connection.prepareStatement("SELECT manga_id from manga where manga_name = ?");
				pst.setString(1, manga_name);
				ResultSet mangaIdRow = pst.executeQuery();
				mangaIdRow.next();
				int manga_id = mangaIdRow.getInt("manga_id");
				pst = connection.prepareStatement(queries.get(1));
				pst.setString(1, manga_genre);
				pst.executeQuery();
				pst = connection.prepareStatement("SELECT genres_id from genres where genre_des = ?");
				pst.setString(1, manga_genre);
				ResultSet idRow = pst.executeQuery();
				idRow.next();
				int genres_id = idRow.getInt("genres_id");
				pst = connection.prepareStatement(queries.get(2));
				pst.setInt(1, genres_id);
				pst.setInt(2, manga_id);
				pst.executeUpdate();
				connection.commit();
				return true;
			} catch (SQLException e) {
				connection.rollback();
				e.printStackTrace();
			}
			return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addChapter(String chapter_title, int chapter_number, int chapter_num_pages, String chapter_location, String manga_genre, int manga_id, String manga_name) {

		PreparedStatement ps = null;

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			String insertQuery = "insert into chapters ( manga_id, chapter_number, chapter_title, chapter_creation_time, chapter_location, chapter_num_pages) values (?,?,?,?,?,?)";
			ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery);
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
			ps.setString(4, timestamp);
			ps.setInt(1, manga_id);
			ps.setInt(2, chapter_number);
			ps.setString(3, chapter_title);
			ps.setString(4, timestamp);
			ps.setString(5, chapter_location);
			ps.setInt(6, chapter_num_pages);
			System.out.println(ps.toString());
			if (ps.executeUpdate() == 1) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return false;

	}


	public boolean updateManga(int manga_id, String new_genre, String new_name, String new_synopsis, int new_status, String genres) {

		PreparedStatement ps = null;

		try (Connection connection = ConnectionMySQL.getConnection()) {
				String update = "update manga set manga_name = ?, manga_synopsis = ?, manga_status = ? where manga_id = ? ";
				ps = connection.prepareStatement(update);
				ps.setString(1, new_name);
				ps.setString(2, new_synopsis);
				ps.setInt(3, new_status);
				ps.setInt(4, manga_id);
				ps.executeUpdate();

				String delete = "delete from manga_genre where manga_id = ?";
				ps = connection.prepareStatement(delete);
				ps.setInt(1, manga_id);
				ps.executeUpdate();

				for (String id_genre : genres.split(",")) {
					String inser_genre = "insert into manga_genre (genres_id, manga_id) VALUES (?,?)";
					ps = connection.prepareStatement(update);
					ps.setInt(1, Integer.parseInt(id_genre) );
					ps.setInt(2, manga_id);
					ps.executeUpdate();
				}

				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteChapter(int chapter_id) {

		PreparedStatement ps = null;

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String delete = "delete from chapters where chapter_id = ?";
			ps = ConnectionMySQL.getConnection().prepareStatement(delete);
			ps.setInt(1, chapter_id);
			System.out.println(ps.toString());
			ps.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		}

	}

	public boolean deleteManga(int manga_id) {
		PreparedStatement ps = null;

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			String delete = "delete from manga where manga_id = ?";
			ps = ConnectionMySQL.getConnection().prepareStatement(delete);
			ps.setInt(1, manga_id);
			System.out.println(ps.toString());
			ps.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		}
	}
}
 
package Models;

import utils.ConnectionMySQL;
import utils.Props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Manga {
	int manga_id = -1;
	int user_id;
	String manga_name;
	String manga_synopsis;
	int manga_status;
	String manga_creation_time;
	List<Integer> genres_ids = new ArrayList<>();
	boolean user_liked;
	int likes;

	public boolean isUser_liked() {
		return user_liked;
	}

	public void setUser_liked(boolean user_liked) {
		this.user_liked = user_liked;
	}


	public int getLikes() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Manga manga = null;


		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_get_manga_likes"));
			ps.setInt(1, this.getManga_id());
			rs = ps.executeQuery();
			if (rs.next()) {
				this.likes = rs.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this.likes;
	}

	public boolean isLikedBy(User user) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_manga_is_liked_by"));
			ps.setInt(1, this.getManga_id());
			ps.setInt(2, user.getUser_id());
			rs = ps.executeQuery();
			rs.last();
			return rs.getRow() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean likedBy(User user) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		if ( !this.isLikedBy(user) ) {
			try {
				ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_manga_liked_by"));
				ps.setInt(1, this.getManga_id());
				ps.setInt(2, user.getUser_id());
				ps.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static Manga get(int id) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Manga manga = null;

		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_manga"));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				manga = new Manga();
				manga.setManga_id(rs.getInt("manga_id"));
				manga.setUser_id(rs.getInt("user_id"));
				manga.setManga_name(rs.getString("manga_name"));
				manga.setManga_synopsis(rs.getString("manga_synopsis"));
				manga.setManga_status(rs.getInt("manga_status"));
				manga.setManga_creation_time(rs.getString("manga_creation_time"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return manga;
	}

	public boolean save() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			if (this.manga_id == -1) {
				// INSERT NEW MANGA
				String insertQuery = Props.getProperty("insert_manga");
				ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, this.getUser_id());
				ps.setString(2, this.getManga_name());
				ps.setString(3, this.getManga_synopsis());
				ps.setInt(4, this.getManga_status());
				ps.executeUpdate();

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setManga_id(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating user failed, no ID obtained.");
					}
				}

				String insertGenresManga = Props.getProperty("insert_genresmanga");
				for (int id: this.genres_ids) {
					ps = ConnectionMySQL.getConnection().prepareStatement(insertGenresManga);
					ps.setInt(1, this.getManga_id());
					ps.setInt(2, id);
					ps.executeUpdate();
				}
				return true;
			} else {
				// 	UPDATE MANGA
				String updateQuery = Props.getProperty("update_manga");
				ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
				ps.setInt(1, this.getUser_id());
				ps.setString(2, this.getManga_name());
				ps.setString(3, this.getManga_synopsis());
				ps.setInt(4, this.getManga_status());
				ps.setInt(5, this.getManga_id());
				ps.executeUpdate();

				String deleteGenresManga = Props.getProperty("delete_genresmanga");
				ps = ConnectionMySQL.getConnection().prepareStatement(deleteGenresManga);
				ps.setInt(1, this.getManga_id());
				ps.executeUpdate();

				String insertGenresManga = Props.getProperty("insert_genresmanga");
				for (int id: this.genres_ids) {
					ps = ConnectionMySQL.getConnection().prepareStatement(insertGenresManga);
					ps.setInt(1, this.getManga_id());
					ps.setInt(2, id);
					ps.executeUpdate();
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public int getManga_id() {
		return manga_id;
	}

	public void setManga_id(int manga_id) {
		this.manga_id = manga_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getManga_name() {
		return manga_name;
	}

	public void setManga_name(String manga_name) {
		this.manga_name = manga_name;
	}

	public String getManga_synopsis() {
		return manga_synopsis;
	}

	public void setManga_synopsis(String manga_synopsis) {
		this.manga_synopsis = manga_synopsis;
	}

	public int getManga_status() {
		return manga_status;
	}

	public void setManga_status(int manga_status) {
		this.manga_status = manga_status;
	}

	public String getManga_creation_time() {
		return manga_creation_time;
	}

	public void setManga_creation_time(String manga_creation_time) {
		this.manga_creation_time = manga_creation_time;
	}

	public void clear_genres() {
		this.genres_ids.clear();
	}
	public void add_genre(int genre_id) {
		this.genres_ids.add(genre_id);
	}
	public void set_genres(String ids) {
		String[] genres_ids = ids.split(",");
		for (String id: genres_ids) {
			this.add_genre( Integer.parseInt(id) );
		}
	}
}

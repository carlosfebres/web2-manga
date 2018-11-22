package Models;

import Interfaces.CommentAble;
import Interfaces.Likeable;
import Interfaces.Model;
import utils.ConnectionMySQL;
import utils.Props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Manga implements Model, Likeable, CommentAble<CommentManga> {
	int manga_id = -1;
	int user_id;
	String manga_name;
	String manga_synopsis;
	int manga_status;
	String manga_creation_time;
	boolean user_liked;
	int likes;

	List<Integer> genres_ids = new ArrayList<>();
	List<CommentManga> comments = new ArrayList<>();


	public List<CommentManga> getComments() {
		if (this.comments.isEmpty()) {
			this.fetchComments();
		}
		return comments;
	}

	public void fetchComments() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_manga_comments"));
			ps.setInt(1, this.getMangaId());
			rs = ps.executeQuery();
			while (rs.next()) {
				this.addComment(CommentManga.get(rs.getInt("comment_id")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addComment(CommentManga comment) {
		this.comments.add( comment );
	}

	public boolean isUserLiked() {
		return user_liked;
	}

	public void setUserLiked(boolean user_liked) {
		this.user_liked = user_liked;
	}

	public int getLikes() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Manga manga = null;

		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_get_manga_likes"));
			ps.setInt(1, this.getMangaId());
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
			ps.setInt(1, this.getMangaId());
			ps.setInt(2, user.getUserId());
			rs = ps.executeQuery();
			rs.last();
			this.setUserLiked(rs.getRow() > 0);
			return rs.getRow() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean likedBy(User user) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		if (!this.isLikedBy(user)) {
			try {
				ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_manga_liked_by"));
				ps.setInt(1, this.getMangaId());
				ps.setInt(2, user.getUserId());
				ps.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean unlikedBy(User user) {
		PreparedStatement ps = null;
		try {
			String updateQuery = Props.getProperty("delete_manga_like");
			ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
			ps.setInt(1, this.getMangaId());
			ps.setInt(2, user.getUserId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
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
				manga.setMangaId(rs.getInt("manga_id"));
				manga.setUserId(rs.getInt("user_id"));
				manga.setMangaName(rs.getString("manga_name"));
				manga.setMangaSynopsis(rs.getString("manga_synopsis"));
				manga.setMangaStatus(rs.getInt("manga_status"));
				manga.setMangaCreationTime(rs.getString("manga_creation_time"));
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
				ps.setInt(1, this.getUserId());
				ps.setString(2, this.getMangaName());
				ps.setString(3, this.getMangaSynopsis());
				ps.setInt(4, this.getMangaStatus());
				ps.executeUpdate();

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setMangaId(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating user failed, no ID obtained.");
					}
				}

				String insertGenresManga = Props.getProperty("insert_genresmanga");
				for (int id : this.genres_ids) {
					ps = ConnectionMySQL.getConnection().prepareStatement(insertGenresManga);
					ps.setInt(1, this.getMangaId());
					ps.setInt(2, id);
					ps.executeUpdate();
				}
				return true;
			} else {
				// 	UPDATE MANGA
				String updateQuery = Props.getProperty("update_manga");
				ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
				ps.setInt(1, this.getUserId());
				ps.setString(2, this.getMangaName());
				ps.setString(3, this.getMangaSynopsis());
				ps.setInt(4, this.getMangaStatus());
				ps.setInt(5, this.getMangaId());
				ps.executeUpdate();

				String deleteGenresManga = Props.getProperty("delete_genresmanga");
				ps = ConnectionMySQL.getConnection().prepareStatement(deleteGenresManga);
				ps.setInt(1, this.getMangaId());
				ps.executeUpdate();

				String insertGenresManga = Props.getProperty("insert_genresmanga");
				for (int id : this.genres_ids) {
					ps = ConnectionMySQL.getConnection().prepareStatement(insertGenresManga);
					ps.setInt(1, this.getMangaId());
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


	public boolean delete() {
		PreparedStatement ps = null;
		try {
			String updateQuery = Props.getProperty("delete_manga");
			ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
			ps.setInt(1, this.getMangaId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public int getMangaId() {
		return manga_id;
	}

	public void setMangaId(int manga_id) {
		this.manga_id = manga_id;
	}

	public int getUserId() {
		return user_id;
	}

	public void setUserId(int user_id) {
		this.user_id = user_id;
	}

	public String getMangaName() {
		return manga_name;
	}

	public void setMangaName(String manga_name) {
		this.manga_name = manga_name;
	}

	public String getMangaSynopsis() {
		return manga_synopsis;
	}

	public void setMangaSynopsis(String manga_synopsis) {
		this.manga_synopsis = manga_synopsis;
	}

	public int getMangaStatus() {
		return manga_status;
	}

	public void setMangaStatus(int manga_status) {
		this.manga_status = manga_status;
	}

	public String getMangaCreationTime() {
		return manga_creation_time;
	}

	public void setMangaCreationTime(String manga_creation_time) {
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
		for (String id : genres_ids) {
			this.add_genre(Integer.parseInt(id));
		}
	}
}

package Models;

import Exceptions.ModelNotFound;
import Interfaces.CommentAble;
import Interfaces.Likeable;
import Interfaces.Model;
import CustomUtils.ConnectionMySQL;
import CustomUtils.Props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Manga implements Model, Likeable, CommentAble<CommentManga> {
	private int mangaId = -1;
	private int userId;
	private int likes;
	private int mangaStatus;
	private String mangaName;
	private String mangaSynopsis;
	private String mangaCreationTime;
	private LikeManga userLiked;


	public static Manga get(int id) throws ModelNotFound {
		Manga manga = new Manga();
		Manga.fetchAndFill(id, manga );
		return manga;
	}

	public static Manga get(String manga_id) throws ModelNotFound {
		return Manga.get( Integer.parseInt(manga_id) );
	}

	public void fetch() throws ModelNotFound {
		Manga.fetchAndFill( this.getMangaId(), this );
	}

	private static void fetchAndFill(int id, Model obj) throws ModelNotFound {
		Manga manga = (Manga) obj;
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_manga"));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				manga.setMangaId(rs.getInt("manga_id"));
				manga.setUserId(rs.getInt("user_id"));
				manga.setMangaName(rs.getString("manga_name"));
				manga.setMangaSynopsis(rs.getString("manga_synopsis"));
				manga.setMangaStatus(rs.getInt("manga_status"));
				manga.setMangaCreationTime(rs.getString("manga_creation_time"));
			} else {
				throw new ModelNotFound("Manga");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean save() {
		PreparedStatement ps;
		try {
			if (this.mangaId == -1) {
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
			} else {
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
			}

			String insertGenresManga = Props.getProperty("insert_genresmanga");
			for (int id : this.genres_ids) {
				ps = ConnectionMySQL.getConnection().prepareStatement(insertGenresManga);
				ps.setInt(1, this.getMangaId());
				ps.setInt(2, id);
				ps.executeUpdate();
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean delete() {
		PreparedStatement ps;
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

	// -------- COMMENTS MANAGEMENT -------- //

	private List<Integer> genres_ids = new ArrayList<>();
	private List<CommentManga> comments = new ArrayList<>();

	public List<CommentManga> getComments() {
		if (this.comments.isEmpty()) {
			this.fetchComments();
		}
		return comments;
	}

	public void fetchComments() {
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_manga_comments"));
			ps.setInt(1, this.getMangaId());
			rs = ps.executeQuery();
			while (rs.next()) {
				try {
					this.addComment(CommentManga.get(rs.getInt("comment_id")));
				} catch (ModelNotFound modelNotFound) {
					modelNotFound.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addComment(CommentManga comment) {
		this.comments.add( comment );
	}

	// -------- LIKES MANAGEMENT -------- //

	public LikeManga getUserLiked() {
		return userLiked;
	}

	public void setUserLiked(LikeManga user_liked) {
		this.userLiked = user_liked;
	}

	public int getLikes() {
		PreparedStatement ps;
		ResultSet rs;
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
		try {
			LikeManga like = LikeManga.of(this, user);
			this.setUserLiked( like );
			return true;
		} catch (ModelNotFound modelNotFound) {
			return false;
		}
	}

	public boolean likedBy(User user) {
		if (!this.isLikedBy(user)) {
			LikeManga like = new LikeManga();
			like.setMangaId(this.getMangaId());
			like.setUserId(user.getUserId());
			return like.save();
		}
		return false;
	}

	public boolean unlikedBy(User user) {
		try {
			LikeManga like = LikeManga.of(this, user);
			return like.delete();
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
		}
		return false;
	}

	// -------- GETTERS AND SETTERS -------- //

	public int getMangaId() {
		return mangaId;
	}

	public void setMangaId(int manga_id) {
		this.mangaId = manga_id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int user_id) {
		this.userId = user_id;
	}

	public String getMangaName() {
		return mangaName;
	}

	public void setMangaName(String manga_name) {
		this.mangaName = manga_name;
	}

	public String getMangaSynopsis() {
		return mangaSynopsis;
	}

	public void setMangaSynopsis(String manga_synopsis) {
		this.mangaSynopsis = manga_synopsis;
	}

	public int getMangaStatus() {
		return mangaStatus;
	}

	public void setMangaStatus(int manga_status) {
		this.mangaStatus = manga_status;
	}

	public String getMangaCreationTime() {
		return mangaCreationTime;
	}

	public void setMangaCreationTime(String manga_creation_time) {
		this.mangaCreationTime = manga_creation_time;
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

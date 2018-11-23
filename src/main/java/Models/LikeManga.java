package Models;

import Exceptions.ModelNotFound;
import Interfaces.LikeModel;
import Interfaces.Model;
import CustomUtils.ConnectionMySQL;
import CustomUtils.Props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LikeManga implements LikeModel {
	private int likeId;
	private int userId;
	private int mangaId;

	public static LikeManga of(int manga, int user) throws ModelNotFound {
		LikeManga like = null;
		try {
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_manga_get_liked_by"));
			ps.setInt(1, manga);
			ps.setInt(2, user);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				like = new LikeManga();
				like.setLikeId(rs.getInt("like_id"));
				like.setUserId(rs.getInt("user_id"));
				like.setMangaId(rs.getInt("manga_id"));
			} else {
				throw new ModelNotFound("Like Manga");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return like;
	}

	public static LikeManga of(Manga manga, User user) throws ModelNotFound {
		return LikeManga.of(manga.getMangaId(), user.getUserId());
	}

	public static LikeManga get(int id) throws ModelNotFound {
		LikeManga like = new LikeManga();
		LikeManga.fetchAndFill(id, like);
		return like;
	}

	public static LikeManga get(String like_id) throws ModelNotFound {
		return LikeManga.get(Integer.parseInt(like_id));
	}

	public void fetch() throws ModelNotFound {
		LikeManga.fetchAndFill(this.getLikeId(), this);
	}

	private static void fetchAndFill(int id, Model obj) throws ModelNotFound {
		LikeManga like = (LikeManga) obj;
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_manga_get_like"));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				like.setUserId(rs.getInt("user_id"));
				like.setMangaId(rs.getInt("manga_id"));
			} else {
				throw new ModelNotFound("Like Manga");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean save() {
		try {
			Manga.get( this.getMangaId() );
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
			return false;
		}
		try {
			LikeManga check = LikeManga.of(this.getMangaId(), this.getUserId());
			this.setLikeId( check.getLikeId() );
			System.out.println("Already Liked");
		} catch (ModelNotFound modelNotFound) {
			try {
				String insertQuery = Props.getProperty("sql_manga_liked_by");
				PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, this.getMangaId());
				ps.setInt(2, this.getUserId());

				ps.executeUpdate();

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setLikeId(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating like failed, no ID obtained.");
					}
				}
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public boolean delete() {
		PreparedStatement ps;
		try {
			String deleteQuery = Props.getProperty("delete_manga_like");
			ps = ConnectionMySQL.getConnection().prepareStatement(deleteQuery);
			ps.setInt(1, this.getLikeId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// -------- GETTERS AND SETTERS -------- //

	public int getLikeId() {
		return likeId;
	}

	public void setLikeId(int likeId) {
		this.likeId = likeId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMangaId() {
		return mangaId;
	}

	public void setMangaId(int mangaId) {
		this.mangaId = mangaId;
	}
}

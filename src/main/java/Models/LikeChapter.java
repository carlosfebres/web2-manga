package Models;

import CustomUtils.ConnectionMySQL;
import CustomUtils.Props;
import Exceptions.ModelNotFound;
import Interfaces.LikeModel;
import Interfaces.Model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LikeChapter implements LikeModel {
	private int likeId;
	private int userId;
	private int chapterId;

	public static LikeChapter of(int chapter, int user) throws ModelNotFound {
		LikeChapter like = null;
		try {
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_chapter_get_liked_by"));
			ps.setInt(1, chapter);
			ps.setInt(2, user);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				like = new LikeChapter();
				like.setLikeId(rs.getInt("like_id"));
				like.setUserId(rs.getInt("user_id"));
				like.setChapterId(rs.getInt("chapter_id"));
			} else {
				throw new ModelNotFound("Like Chapter");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return like;
	}

	public static LikeChapter of(Chapter chapter, User user) throws ModelNotFound {
		return LikeChapter.of(chapter.getChapterId(), user.getUserId());
	}

	public static LikeChapter get(int id) throws ModelNotFound {
		LikeChapter like = new LikeChapter();
		LikeChapter.fetchAndFill(id, like);
		return like;
	}

	public static LikeChapter get(String like_id) throws ModelNotFound {
		return LikeChapter.get(Integer.parseInt(like_id));
	}

	public void fetch() throws ModelNotFound {
		LikeChapter.fetchAndFill(this.getLikeId(), this);
	}

	private static void fetchAndFill(int id, Model obj) throws ModelNotFound {
		LikeChapter like = (LikeChapter) obj;
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_chapter_get_like"));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				like.setUserId(rs.getInt("user_id"));
				like.setChapterId(rs.getInt("chapter_id"));
			} else {
				throw new ModelNotFound("Like Chapter");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean save() {
		try {
			Chapter.get( this.getChapterId() );
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
			return false;
		}
		try {
			LikeChapter check = LikeChapter.of(this.getChapterId(), this.getUserId());
			this.setLikeId( check.getLikeId() );
			System.out.println("Already Liked");
		} catch (ModelNotFound modelNotFound) {
			try {
				String insertQuery = Props.getProperty("sql_chapter_liked_by");
				PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, this.getChapterId());
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
			String deleteQuery = Props.getProperty("delete_chapter_like");
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

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}
}

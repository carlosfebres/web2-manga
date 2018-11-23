package Models;

import Exceptions.ModelNotFound;
import Interfaces.CommentModel;
import Interfaces.Model;
import CustomUtils.ConnectionMySQL;
import CustomUtils.Props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CommentManga implements CommentModel {
	private int commentId = -1;
	private int mangaId;
	private int userId;
	private String commentContent;
	private String commentCreationTime;


	public static CommentManga get(int id) throws ModelNotFound {
		CommentManga comment = new CommentManga();
		CommentManga.fetchAndFill(id, comment );
		return comment;
	}

	public static CommentManga get(String comment_id) throws ModelNotFound {
		return CommentManga.get( Integer.parseInt(comment_id) );
	}

	public void fetch() throws ModelNotFound {
		CommentManga.fetchAndFill( this.getCommentId(), this );
	}

	private static void fetchAndFill(int id, Model obj) throws ModelNotFound {
		CommentManga model = (CommentManga) obj;
		try {
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_comment_manga"));
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				model.setCommentId(rs.getInt("comment_id"));
				model.setMangaId(rs.getInt("manga_id"));
				model.setUserId(rs.getInt("user_id"));
				model.setCommentContent(rs.getString("comment_content"));
				model.setCommentCreationTime(rs.getString("comment_creation_time"));
			} else {
				throw new ModelNotFound("Comment Manga");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean save() {
		PreparedStatement ps;
		try {
			if (this.commentId == -1) {
				// INSERT NEW COMMENT
				String insertQuery = Props.getProperty( "insert_comment_manga" );
				ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, this.getUserId());
				ps.setInt(2, this.getMangaId());
				ps.setString(3, this.getCommentContent());

				ps.executeUpdate();

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setCommentId(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating chapter comment failed, no ID obtained.");
					}
				}

				return true;
			} else {
				// 	UPDATE COMMENT
				String updateQuery = Props.getProperty( "update_comment_manga" );
				ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
				ps.setInt(1, this.getUserId());
				ps.setInt(2, this.getMangaId());
				ps.setString(3, this.getCommentContent());
				ps.setInt(4, this.getCommentId());
				ps.executeUpdate();

				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean delete() {
		PreparedStatement ps;
		try {
			String deleteQuery = Props.getProperty("delete_comment_manga");
			ps = ConnectionMySQL.getConnection().prepareStatement(deleteQuery);
			ps.setInt(1, this.getCommentId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// -------- GETTERS AND SETTERS -------- //

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getCommentCreationTime() {
		return commentCreationTime;
	}

	public void setCommentCreationTime(String commentCreationTime) {
		this.commentCreationTime = commentCreationTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setId(int id) {
		this.setMangaId(id);
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

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
}

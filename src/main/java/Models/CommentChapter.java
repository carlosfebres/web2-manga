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

public class CommentChapter implements CommentModel {
	private int commentId = -1;
	private int chapterId;
	private int userId;
	private String commentContent;
	private String commentCreationTime;

	public static CommentChapter get(int id) throws ModelNotFound {
		CommentChapter comment = new CommentChapter();
		CommentChapter.fetchAndFill(id, comment );
		return comment;
	}

	public static CommentChapter get(String commentId) throws ModelNotFound {
		return CommentChapter.get( Integer.parseInt(commentId) );
	}

	public void fetch() throws ModelNotFound {
		CommentChapter.fetchAndFill( this.getCommentId(), this );
	}

	private static void fetchAndFill(int id, Model obj) throws ModelNotFound {
		CommentChapter model = (CommentChapter) obj;
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_comment_chapter"));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				model.setCommentId(rs.getInt("comment_id"));
				model.setChapterId(rs.getInt("chapter_id"));
				model.setUserId(rs.getInt("user_id"));
				model.setCommentContent(rs.getString("comment_content"));
				model.setCommentCreationTime(rs.getString("comment_creation_time"));
			} else {
				throw new ModelNotFound("Comment Chapter");
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
				String insertQuery = Props.getProperty("insert_comment_chapter");
				ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, this.getUserId());
				ps.setInt(2, this.getChapterId());
				ps.setString(3, this.getCommentContent());

				ps.executeUpdate();

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setCommentId(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating chapter failed, no ID obtained.");
					}
				}

				return true;
			} else {
				String updateQuery = Props.getProperty("update_comment_chapter");
				ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
				ps.setInt(1, this.getUserId());
				ps.setInt(2, this.getChapterId());
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
		try {
			String deleteQuery = Props.getProperty("delete_comment_chapter");
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(deleteQuery);
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

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public int getUserId() {
		return userId;
	}

	@Override
	public void setId(int id) {
		this.setCommentId(id);
	}

	@Override
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCommentContent() {
		return commentContent;
	}

	@Override
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getCommentCreationTime() {
		return commentCreationTime;
	}

	public void setCommentCreationTime(String commentCreationTime) {
		this.commentCreationTime = commentCreationTime;
	}
}

package Models;

import Interfaces.CommentModel;
import Interfaces.Model;
import utils.ConnectionMySQL;
import utils.Props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CommentManga implements CommentModel {
	private int manga_id;
	int comment_id = -1;
	int user_id;
	String comment_content;
	String comment_creation_time;

	public static CommentManga get(int id) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		CommentManga comment = null;

		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty( "get_comment_manga" ));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				comment = new CommentManga();
				comment.setComment_id(rs.getInt("comment_id"));
				comment.setManga_id(rs.getInt("manga_id"));
				comment.setUser_id(rs.getInt("user_id"));
				comment.setComment_content(rs.getString("comment_content"));
				comment.setComment_creation_time(rs.getString("comment_creation_time"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return comment;
	}


	public boolean save() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			if (this.comment_id == -1) {
				// INSERT NEW COMMENT
				String insertQuery = Props.getProperty( "insert_comment_manga" );
				ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, this.getUser_id());
				ps.setInt(2, this.getManga_id());
				ps.setString(3, this.getComment_content());

				ps.executeUpdate();

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setComment_id(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating chapter comment failed, no ID obtained.");
					}
				}

				return true;
			} else {
				// 	UPDATE COMMENT
				String updateQuery = Props.getProperty( "update_comment_manga" );
				ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
				ps.setInt(1, this.getUser_id());
				ps.setInt(2, this.getManga_id());
				ps.setString(3, this.getComment_content());
				ps.setInt(4, this.getComment_id());
				ps.executeUpdate();

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
			String deleteQuery = Props.getProperty("delete_comment_manga");
			ps = ConnectionMySQL.getConnection().prepareStatement(deleteQuery);
			ps.setInt(1, this.getComment_id());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public String getComment_creation_time() {
		return comment_creation_time;
	}

	public void setComment_creation_time(String comment_creation_time) {
		this.comment_creation_time = comment_creation_time;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setId(int id) {
		this.setManga_id(id);
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getManga_id() {
		return manga_id;
	}

	public void setManga_id(int manga_id) {
		this.manga_id = manga_id;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
}

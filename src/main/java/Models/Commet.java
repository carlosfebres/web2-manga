package Models;

import utils.ConnectionMySQL;
import utils.Props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Commet {
	int user_id;
	private int id;
	String comment_content;


	public static Commet get(int id) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Chapter chapter = null;

		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_chapter"));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				chapter = new Chapter();
				chapter.setChapter_id(rs.getInt("chapter_id"));
				chapter.setManga_id(rs.getInt("manga_id"));
				chapter.setChapter_number(rs.getInt("chapter_number"));
				chapter.setChapter_title(rs.getString("chapter_title"));
				chapter.setChapter_location(rs.getString("chapter_location"));
				chapter.setChapter_num_pages(rs.getInt("chapter_num_pages"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return chapter;
	}


	public boolean save() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			if (this.manga_id == -1) {
				// INSERT NEW MANGA
				String insertQuery = Props.getProperty("insert_chapter");
				ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, this.getManga_id());
				ps.setInt(2, this.getChapter_number());
				ps.setString(3, this.getChapter_title());
				ps.setString(4, this.getChapter_location());
				ps.setInt(5, this.getChapter_num_pages());

				ps.executeUpdate();

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setManga_id(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating chapter failed, no ID obtained.");
					}
				}

				return true;
			} else {
				// 	UPDATE MANGA
				String updateQuery = Props.getProperty("update_chapter");
				ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
				ps.setInt(1, this.getManga_id());
				ps.setInt(2, this.getChapter_number());
				ps.setString(3, this.getChapter_title());
				ps.setString(4, this.getChapter_location());
				ps.setInt(5, this.getChapter_num_pages());
				ps.setInt(6, this.getChapter_id());
				ps.executeUpdate();

				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}



	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
}

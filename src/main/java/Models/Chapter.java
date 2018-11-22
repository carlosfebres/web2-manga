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

public class Chapter implements Model, Likeable, CommentAble<CommentChapter> {
	int chapter_id = -1;
	int manga_id;
	int chapter_number;
	int chapter_num_pages;
	String chapter_title;
	String chapter_location;
	String chapter_creation_time;
	public int likes;

	public List<CommentChapter> comments = new ArrayList<>();

	public static Chapter get(int id) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Chapter chapter = null;

		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_chapter"));
			ps.setInt(1, id);
			System.out.println(Props.getProperty("get_chapter"));
			rs = ps.executeQuery();
			if (rs.next()) {
				chapter = new Chapter();
				chapter.setChapter_id(rs.getInt("chapter_id"));
				chapter.setManga_id(rs.getInt("manga_id"));
				chapter.setChapter_number(rs.getInt("chapter_number"));
				chapter.setChapter_title(rs.getString("chapter_title"));
				chapter.setChapter_location(rs.getString("chapter_location"));
				chapter.setChapter_num_pages(rs.getInt("chapter_num_pages"));
				chapter.setChapter_creation_time(rs.getString("chapter_creation_time"));
			} else {
				System.out.println("Chapter Not Found");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return chapter;
	}


	public boolean save() {
		PreparedStatement ps = null;
		try {
			if (this.chapter_id == -1) {
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
						this.setChapter_id(generatedKeys.getInt(1));
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


	@Override
	public boolean unlikedBy(User user) {
		PreparedStatement ps = null;
		try {
			String updateQuery = Props.getProperty("delete_chapter_like");
			ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
			ps.setInt(1, this.getChapter_id());
			ps.setInt(2, user.getUserId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean delete() {
		PreparedStatement ps = null;
		try {
			String deleteQuery = Props.getProperty("delete_chapter");
			ps = ConnectionMySQL.getConnection().prepareStatement(deleteQuery);
			ps.setInt(1, this.getChapter_id());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public int getLikes() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Manga manga = null;


		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_get_chapter_likes"));
			ps.setInt(1, this.getChapter_id());
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
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_chapter_is_liked_by"));
			ps.setInt(1, this.getChapter_id());
			ps.setInt(2, user.getUserId());
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
				ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_chapter_liked_by"));
				ps.setInt(1, this.getChapter_id());
				ps.setInt(2, user.getUserId());
				ps.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}




	public int getChapter_id() {
		return chapter_id;
	}

	public void setChapter_id(int chapter_id) {
		this.chapter_id = chapter_id;
	}

	public int getManga_id() {
		return manga_id;
	}

	public void setManga_id(int manga_id) {
		this.manga_id = manga_id;
	}

	public int getChapter_number() {
		return chapter_number;
	}

	public void setChapter_number(int chapter_number) {
		this.chapter_number = chapter_number;
	}

	public String getChapter_title() {
		return chapter_title;
	}

	public void setChapter_title(String chapter_title) {
		this.chapter_title = chapter_title;
	}

	public String getChapter_location() {
		return chapter_location;
	}

	public void setChapter_location(String chapter_location) {
		this.chapter_location = chapter_location;
	}

	public int getChapter_num_pages() {
		return chapter_num_pages;
	}

	public void setChapter_num_pages(int chapter_num_pages) {
		this.chapter_num_pages = chapter_num_pages;
	}

	public String getChapter_creation_time() {
		return chapter_creation_time;
	}

	public void setChapter_creation_time(String chapter_creation_time) {
		this.chapter_creation_time = chapter_creation_time;
	}

	@Override
	public List<CommentChapter> getComments() {
		if (this.comments.isEmpty()) {
			this.fetchComments();
		}
		return comments;
	}

	@Override
	public void fetchComments() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_chapter_comments"));
			ps.setInt(1, this.getChapter_id());
			rs = ps.executeQuery();
			while (rs.next()) {
				this.addComment(CommentChapter.get(rs.getInt("comment_id")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void addComment(CommentChapter comment) {
		this.comments.add(comment);
	}
}

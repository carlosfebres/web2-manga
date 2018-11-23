package Models;

import CustomHelpers.SendEmail;
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

public class Chapter implements Model, Likeable, CommentAble<CommentChapter> {
	private int chapterId = -1;
	private int mangaId;
	private int chapterNumber;
	private int chapterNumPages;
	private String chapterTitle;
	private String chapterLocation;
	private String chapterCreationTime;
	private int likes;

	private List<CommentChapter> comments = new ArrayList<>();


	public static Chapter get(int id) throws ModelNotFound {
		Chapter comment = new Chapter();
		Chapter.fetchAndFill(id, comment );
		return comment;
	}

	public static Chapter get(String chapter_id) throws ModelNotFound {
		return Chapter.get( Integer.parseInt(chapter_id) );
	}

	public void fetch() throws ModelNotFound {
		Chapter.fetchAndFill( this.getChapterId(), this );
	}

	private static void fetchAndFill(int id, Model obj) throws ModelNotFound {
		Chapter chapter = (Chapter) obj;
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_chapter"));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				chapter.setChapterId(rs.getInt("chapter_id"));
				chapter.setMangaId(rs.getInt("manga_id"));
				chapter.setChapterNumber(rs.getInt("chapter_number"));
				chapter.setChapterTitle(rs.getString("chapter_title"));
				chapter.setChapterLocation(rs.getString("chapter_location"));
				chapter.setChapterNumPages(rs.getInt("chapter_num_pages"));
				chapter.setChapterCreationTime(rs.getString("chapter_creation_time"));
			} else {
				throw new ModelNotFound("Chapter");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean save() {
		PreparedStatement ps;
		try {
			if (this.chapterId == -1) {
				String insertQuery = Props.getProperty("insert_chapter");
				ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, this.getMangaId());
				ps.setInt(2, this.getChapterNumber());
				ps.setString(3, this.getChapterTitle());
				ps.setString(4, this.getChapterLocation());
				ps.setInt(5, this.getChapterNumPages());
				ps.executeUpdate();
				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setChapterId(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating chapter failed, no ID obtained.");
					}
				}
				this.notifyUsers();
				return true;
			} else {
				String updateQuery = Props.getProperty("update_chapter");
				ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
				ps.setInt(1, this.getMangaId());
				ps.setInt(2, this.getChapterNumber());
				ps.setString(3, this.getChapterTitle());
				ps.setString(4, this.getChapterLocation());
				ps.setInt(5, this.getChapterNumPages());
				ps.setInt(6, this.getChapterId());
				ps.executeUpdate();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void notifyUsers() {
		try {
			Manga manga = Manga.get( this.getMangaId() );
			manga.fillSubscribers();
			SendEmail.newChapter(manga, this, manga.getSubscribers());
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
		}
	}

	@Override
	public boolean unlikedBy(User user) {
		PreparedStatement ps;
		try {
			String updateQuery = Props.getProperty("delete_chapter_like");
			ps = ConnectionMySQL.getConnection().prepareStatement(updateQuery);
			ps.setInt(1, this.getChapterId());
			ps.setInt(2, user.getUserId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean delete() {
		PreparedStatement ps;
		try {
			String deleteQuery = Props.getProperty("delete_chapter");
			ps = ConnectionMySQL.getConnection().prepareStatement(deleteQuery);
			ps.setInt(1, this.getChapterId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_chapter_comments"));
			ps.setInt(1, this.getChapterId());
			rs = ps.executeQuery();
			while (rs.next()) {
				this.addComment(CommentChapter.get(rs.getInt("comment_id")));
			}
		} catch (SQLException | ModelNotFound e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addComment(CommentChapter comment) {
		this.comments.add(comment);
	}

	public int getLikes() {
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_get_chapter_likes"));
			ps.setInt(1, this.getChapterId());
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
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_chapter_is_liked_by"));
			ps.setInt(1, this.getChapterId());
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
		PreparedStatement ps;
		if ( !this.isLikedBy(user) ) {
			try {
				ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_chapter_liked_by"));
				ps.setInt(1, this.getChapterId());
				ps.setInt(2, user.getUserId());
				ps.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public int getMangaId() {
		return mangaId;
	}

	public void setMangaId(int mangaId) {
		this.mangaId = mangaId;
	}

	public int getChapterNumber() {
		return chapterNumber;
	}

	public void setChapterNumber(int chapterNumber) {
		this.chapterNumber = chapterNumber;
	}

	public String getChapterTitle() {
		return chapterTitle;
	}

	public void setChapterTitle(String chapterTitle) {
		this.chapterTitle = chapterTitle;
	}

	public String getChapterLocation() {
		return chapterLocation;
	}

	public void setChapterLocation(String chapterLocation) {
		this.chapterLocation = chapterLocation;
	}

	public int getChapterNumPages() {
		return chapterNumPages;
	}

	public void setChapterNumPages(int chapterNumPages) {
		this.chapterNumPages = chapterNumPages;
	}

	public String getChapterCreationTime() {
		return chapterCreationTime;
	}

	public void setChapterCreationTime(String chapterCreationTime) {
		this.chapterCreationTime = chapterCreationTime;
	}
}

package Models;

import CustomUtils.ConnectionMySQL;
import CustomUtils.Props;
import Exceptions.ModelNotFound;
import Interfaces.Model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Tracker implements Model {
	private int trackerId;
	private int userId;
	private int mangaId;

	List<Chapter> seen = new ArrayList<>();
	List<Chapter> notSeen = new ArrayList<>();


	public List<Chapter> getSeen() {
		this.fillChapters();
		return seen;
	}

	public List<Chapter> getNotSeen() {
		this.fillChapters();
		return notSeen;
	}

	private void fillChapters () {
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_tracker_seens"));
			ps.setInt(1, this.getTrackerId());
			rs = ps.executeQuery();
			this.seen.clear();
			this.notSeen.clear();
			while (rs.next()) {
				Chapter chapter = new Chapter();
				chapter.setChapterId(rs.getInt("chapter_id"));
				chapter.setMangaId(rs.getInt("manga_id"));
				chapter.setChapterNumber(rs.getInt("chapter_number"));
				chapter.setChapterTitle(rs.getString("chapter_title"));
				chapter.setChapterLocation(rs.getString("chapter_location"));
				chapter.setChapterNumPages(rs.getInt("chapter_num_pages"));
				chapter.setChapterCreationTime(rs.getString("chapter_creation_time"));
				System.out.println(rs.getString("tracker_chapter_id") );
				if (rs.getString("tracker_chapter_id") == null) {
					this.notSeen.add(chapter);
				} else {
					this.seen.add(chapter);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean save() {
		try {
			String insertQuery = Props.getProperty("create_tracker");
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, this.getMangaId());
			ps.setInt(2, this.getUserId());
			if (ps.executeUpdate() == 1) {
				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setTrackerId(generatedKeys.getInt(1));
						return true;
					} else {
						throw new SQLException("Creating user failed, no ID obtained.");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean delete() {
		return false;
	}

	public static Tracker getFor(int mangaId, int userId) throws ModelNotFound {
		Tracker tracker = new Tracker();
		try {
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_tracker_for"));
			ps.setInt(1, mangaId);
			ps.setInt(2, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				tracker.setTrackerId(rs.getInt("tracker_id"));
				tracker.setUserId(rs.getInt("user_id"));
				tracker.setMangaId(rs.getInt("manga_id"));
			} else {
				tracker.setMangaId(mangaId);
				tracker.setUserId(userId);
				tracker.save();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tracker;
	}

	public void addChapter(Chapter chapter) {
		try {
			String insertQuery = Props.getProperty("insert_tracked_chapter");
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, this.getTrackerId());
			ps.setInt(2, chapter.getChapterId());
			if (ps.executeUpdate() == 1) {
				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setTrackerId(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating user failed, no ID obtained.");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Tracker get(int id) throws ModelNotFound {
		Tracker user = new Tracker();
		Tracker.fetchAndFill(id, user);
		return user;
	}

	public static Tracker get(String id) throws ModelNotFound {
		return Tracker.get(Integer.parseInt(id));
	}

	public void fetch() throws ModelNotFound {
		Tracker.fetchAndFill(this.getTrackerId(), this);
	}

	private static void fetchAndFill(int id, Model obj) throws ModelNotFound {
		Tracker tracker = (Tracker) obj;
		try {
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("get_tracker"));
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				tracker.setTrackerId(rs.getInt("tracker_id"));
				tracker.setUserId(rs.getInt("user_id"));
				tracker.setMangaId(rs.getInt("manga_id"));
			} else {
				throw new ModelNotFound("Tracker");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public int getTrackerId() {
		return trackerId;
	}

	public void setTrackerId(int trackerId) {
		this.trackerId = trackerId;
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

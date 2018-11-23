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

public class Subscribe implements Model {
	private int subscribeId;
	private int userId;
	private int mangaId;

	public static Subscribe of(int manga, int user) throws ModelNotFound {
		Subscribe like = null;
		try {
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_get_subscribe_by_user"));
			ps.setInt(1, manga);
			ps.setInt(2, user);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				like = new Subscribe();
				like.setSubscribeId(rs.getInt("subsribe_id"));
				like.setUserId(rs.getInt("user_id"));
				like.setMangaId(rs.getInt("manga_id"));
			} else {
				throw new ModelNotFound("Subscribe");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return like;
	}

	public static Subscribe of(Manga manga, User user) throws ModelNotFound {
		return Subscribe.of(manga.getMangaId(), user.getUserId());
	}

	public static Subscribe get(int id) throws ModelNotFound {
		Subscribe like = new Subscribe();
		Subscribe.fetchAndFill(id, like);
		return like;
	}

	public static Subscribe get(String like_id) throws ModelNotFound {
		return Subscribe.get(Integer.parseInt(like_id));
	}

	public void fetch() throws ModelNotFound {
		Subscribe.fetchAndFill(this.getSubscribeId(), this);
	}

	private static void fetchAndFill(int id, Model obj) throws ModelNotFound {
		Subscribe like = (Subscribe) obj;
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("sql_get_subscribe"));
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				like.setUserId(rs.getInt("user_id"));
				like.setMangaId(rs.getInt("manga_id"));
			} else {
				throw new ModelNotFound("Subscribe");
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
			Subscribe check = Subscribe.of(this.getMangaId(), this.getUserId());
			this.setSubscribeId( check.getSubscribeId() );
			System.out.println("Already Subscribed");
		} catch (ModelNotFound modelNotFound) {
			try {
				String insertQuery = Props.getProperty("sql_subscribe");
				PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, this.getMangaId());
				ps.setInt(2, this.getUserId());

				ps.executeUpdate();

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						this.setSubscribeId(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating subscrition failed, no ID obtained.");
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
			String deleteQuery = Props.getProperty("sql_remove_subscribe");
			ps = ConnectionMySQL.getConnection().prepareStatement(deleteQuery);
			ps.setInt(1, this.getSubscribeId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// -------- GETTERS AND SETTERS -------- //

	public int getSubscribeId() {
		return subscribeId;
	}

	public void setSubscribeId(int subscribeId) {
		this.subscribeId = subscribeId;
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

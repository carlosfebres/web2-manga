package Models;

import CustomUtils.ConnectionMySQL;
import CustomUtils.Props;
import Exceptions.ModelNotFound;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchMangas {

	private String name;
	List<Manga> list;

	public List<Manga> search() {
		this.list = new ArrayList<>();
		try {
			PreparedStatement ps = ConnectionMySQL.getConnection().prepareStatement(Props.getProperty("search_mangas"));
			ps.setString(1, "%"+this.getName()+"%");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Manga manga = new Manga();
				manga.setMangaId(rs.getInt("manga_id"));
				manga.setUserId(rs.getInt("user_id"));
				manga.setMangaName(rs.getString("manga_name"));
				manga.setMangaSynopsis(rs.getString("manga_synopsis"));
				manga.setMangaStatus(rs.getInt("manga_status"));
				manga.setMangaCreationTime(rs.getString("manga_creation_time"));
				list.add(manga);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Manga> getList() {
		return list;
	}
}

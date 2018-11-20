package Models;

public class Manga {
	int manga_id;
	int user_id;
	String mange_name;
	String mange_synopsis;
	int mange_status;
	String mange_creation_time;

	public int getManga_id() {
		return manga_id;
	}

	public void setManga_id(int manga_id) {
		this.manga_id = manga_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getMange_name() {
		return mange_name;
	}

	public void setMange_name(String mange_name) {
		this.mange_name = mange_name;
	}

	public String getMange_synopsis() {
		return mange_synopsis;
	}

	public void setMange_synopsis(String mange_synopsis) {
		this.mange_synopsis = mange_synopsis;
	}

	public int getMange_status() {
		return mange_status;
	}

	public void setMange_status(int mange_status) {
		this.mange_status = mange_status;
	}

	public String getMange_creation_time() {
		return mange_creation_time;
	}

	public void setMange_creation_time(String mange_creation_time) {
		this.mange_creation_time = mange_creation_time;
	}
}

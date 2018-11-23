package CustomHelpers;

import java.io.File;

public class FolderCreator {

	public String createChapterFolder(String manga_name, String chapter_title) {
		String path = System.getProperty("user.dir") + "/uploads/" + manga_name + "/chapters/" + chapter_title;
		new File(path).mkdirs();
		return path;
	}

	public boolean createMangaFolder(String manga_name) {
		return new File(System.getProperty("user.dir") + "/uploads/" + manga_name + "/chapters").mkdirs();
	}


	public boolean renameManga(String manga_name, String new_name) {
		File dir = new File(System.getProperty("user.dir") + "/uploads/" + manga_name);
		File newName = new File(System.getProperty("user.dir") + "/uploads/" + new_name);
		if (dir.isDirectory()) {
			boolean success = dir.renameTo(newName);
			if (success) {
				dir = new File(System.getProperty("user.dir") + "/uploads/" + manga_name);
				newName = new File(System.getProperty("user.dir") + "/uploads/" + new_name);
				if (dir.isDirectory()) {
					success = dir.renameTo(newName);
					if (success) {
						return true;
					} else {
						return false;
					}
				} else {
					dir.mkdir();
					dir.renameTo(newName);
				}
			} else {
				return false;
			}
		} else {
			dir.mkdir();
			dir.renameTo(newName);
		}
		return false;
	}

	public boolean deleteChapter(int chapter_id, int chapter_number, String manga_genre, String manga_name) {
		String path = System.getProperty("user.dir") + "/uploads/" + manga_name + "/chapters/" + chapter_number;
		boolean deletedFile = new File(path + "/" + new File(path + "/" + new File(path).list()[0]).list()[0]).delete();
		if (deletedFile) {
			return new File(path).delete();
		} else {
			return false;
		}
	}
}

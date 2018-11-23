package Interfaces;

import Models.LikeManga;
import Models.Manga;
import Models.User;

public interface LikeModel extends Model {
	void setUserId(int user_id);
	static LikeManga of(Manga manga, User user){return null;};
}

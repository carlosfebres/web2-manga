package Interfaces;

import Models.User;

public interface Likeable {
	int getLikes();
	boolean isLikedBy(User user);
	boolean likedBy(User user);
	boolean unlikedBy(User user);
}

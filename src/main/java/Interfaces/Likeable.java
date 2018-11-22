package Interfaces;

import Models.User;
import utils.ConnectionMySQL;
import utils.Props;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Likeable {
	int getLikes();
	boolean isLikedBy(User user);
	boolean likedBy(User user);
	boolean unlikedBy(User user);
}

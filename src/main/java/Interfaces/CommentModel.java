package Interfaces;

import Exceptions.ModelNotFound;

public interface CommentModel extends Model {
	void setId(int id);
	void setUserId(int id);
	void setCommentContent(String content);
}

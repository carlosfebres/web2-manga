package Interfaces;

import java.util.List;

public interface CommentAble<T> {
	public List<T> getComments();

	public void fetchComments();

	public void addComment(T comment);

}

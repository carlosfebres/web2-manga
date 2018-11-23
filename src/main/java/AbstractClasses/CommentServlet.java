package AbstractClasses;

import Exceptions.ModelNotFound;
import CustomHelpers.RequestMapper;
import Interfaces.CommentModel;
import Models.Manga;
import Models.Response;
import Models.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class CommentServlet<T extends CommentModel> extends HttpServlet {


	public void doPost(HttpServletRequest req, HttpServletResponse resp, Class<? extends CommentModel> commentClass) throws IOException {
		T comment = (T) RequestMapper.toModel(req, commentClass);
		comment.setUserId( User.fromSession(req).getUserId() );

		Response<T> response = new Response<>(resp);
		if (comment.save()) {
			response.setStatus(200);
			response.setMessage("Comment Created!");
			response.setData(comment);
		} else {
			response.setStatus(400);
			response.setMessage("Error Creating Comment");
		}
		response.print();
	}

	public void doDelete(HttpServletRequest req, HttpServletResponse resp, Class<? extends CommentModel> commentClass) throws IOException {
		Response response = new Response<>(resp);
		T comment = (T) RequestMapper.toModel(req, commentClass );
		try {
			comment.fetch();
			if (comment.delete()) {
				response.setStatus(200);
				response.setMessage("Comment Deleted!");
			} else {
				response.setStatus(400);
				response.setMessage("Error deleting Comment");
			}
		} catch (ModelNotFound modelNotFound) {
			response.setStatus(404);
			response.setMessage("Comment Not Found");
			modelNotFound.printStackTrace();
		} finally {
			response.print();
		}
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp, Class<? extends CommentModel> commentClass) throws IOException {
		Response<CommentModel> response = new Response<>(resp);
		T comment = (T) RequestMapper.toModel(req, commentClass );
		comment.setUserId( User.fromSession(req).getUserId() );

		if (comment.save()) {
			response.setStatus(200);
			response.setMessage("Comment Updated!");
			response.setData(comment);
		} else {
			response.setStatus(400);
			response.setMessage("Error updating Comment");
		}
		response.print();
	}
}

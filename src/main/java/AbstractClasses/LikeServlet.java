package AbstractClasses;


import Exceptions.ModelNotFound;
import CustomHelpers.RequestMapper;
import Interfaces.LikeModel;
import Interfaces.Model;
import Models.LikeManga;
import Models.Response;
import Models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class LikeServlet<T extends LikeModel> extends HttpServlet {

	public void doDelete(HttpServletRequest req, HttpServletResponse resp, Class<? extends Model> likeClass) throws IOException {
		T like = (T) RequestMapper.toModel(req, likeClass);
		Response response = new Response<>(resp);
		try {
			like.fetch();
			if (like.delete()) {
				response.setStatus(200);
				response.setMessage("Unliked");
			} else {
				response.setStatus(400);
				response.setMessage("Error no unlike was made");
			}
		} catch (ModelNotFound modelNotFound) {
			response.setStatus(404);
			response.setMessage("Like not Found");
			modelNotFound.printStackTrace();
		} finally {
			response.print();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp, Class<? extends Model> likeClass) throws ServletException, IOException {
		T like = (T) RequestMapper.toModel(req, likeClass);
		like.setUserId(User.fromSession(req).getUserId());
		Response<T> response = new Response<>(resp);
		if (like.save()) {
			response.setStatus(200);
			response.setMessage("Liked!");
			response.setData(like);
		} else {
			response.setStatus(400);
			response.setMessage("Manga not found or Error on server");
		}
		response.print();
	}


}
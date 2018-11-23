package Servlet;

import CustomHelpers.RequestMapper;
import Exceptions.ModelNotFound;
import Interfaces.Model;
import Models.Response;
import Models.Subscribe;
import Models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SubscribeServlet", urlPatterns = {"/manga/subscribe"})
public class SubscribeServlet extends HttpServlet {
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Subscribe subscribe = (Subscribe) RequestMapper.toModel(req, Subscribe.class);
		Response response = new Response<>(resp);
		try {
			subscribe.fetch();
			if (subscribe.delete()) {
				response.setStatus(200);
				response.setMessage("Unsubscribed");
			} else {
				response.setStatus(400);
				response.setMessage("Error no unsubscribe was made");
			}
		} catch (ModelNotFound modelNotFound) {
			response.setStatus(404);
			response.setMessage("Subscription not Found");
			modelNotFound.printStackTrace();
		} finally {
			response.print();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Subscribe subscribe = (Subscribe) RequestMapper.toModel(req, Subscribe.class);
		subscribe.setUserId(User.fromSession(req).getUserId());
		Response<Subscribe> response = new Response<>(resp);
		if (subscribe.save()) {
			response.setStatus(200);
			response.setMessage("Subscribed!");
			response.setData(subscribe);
		} else {
			response.setStatus(400);
			response.setMessage("Manga not found or Error on server");
		}
		response.print();
	}

}

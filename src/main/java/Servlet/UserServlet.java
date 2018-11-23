package Servlet;

import CustomHelpers.RequestMapper;
import Exceptions.ModelNotFound;
import Models.Response;
import Models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "user", urlPatterns = {"/user"})
public class UserServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Response<User> res = new Response<>(response);
		User user = User.fromSession(request);
		if (user != null) {
			res.setStatus(200);
			res.setMessage("Logged User");
			res.setData(user);
		} else {
			res.setStatus(401);
			res.setMessage("Must Log In");
		}
		res.print();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) RequestMapper.toModel( request, User.class );
		Response<User> resp = new Response<>(response);
		if (user.save()) {
			resp.setStatus(200);
			resp.setMessage("User Registered!");
			resp.setData(user);
			user.storeSession(request);
		} else {
			resp.setStatus(400);
			resp.setMessage("This username already exists, please use another one.");
		}
		resp.print();
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User user = (User) RequestMapper.toModel( req, User.class );
		Response<User> response = new Response<>(resp);
		try {
			User.get(user.getUserId());
			if (user.save()) {
				response.setStatus(200);
				response.setMessage("User Updated!");
				response.setData(user);
				user.storeSession(req);
			} else {
				response.setStatus(400);
				response.setMessage("This username already exists, please use another one.");
			}
		} catch (ModelNotFound modelNotFound) {
			response.setStatus(404);
			response.setMessage("User not found.");
		} finally {
			response.print();
		}
	}

}

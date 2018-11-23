package Servlet;

import CustomHelpers.RequestMapper;
import Models.Response;
import Models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "login", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) RequestMapper.toModel(request, User.class);
		Response<User> resp = new Response<>(response);
		if (user.login()) {
			resp.setMessage("User Logged!");
			resp.setStatus(200);
			resp.setData(user);
			user.storeSession(request);
		} else {
			resp.setMessage("Authentication Failed");
			resp.setStatus(401);
		}
		resp.print();
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Response<User> response = new Response<>(resp);
		HttpSession session = req.getSession();
		response.setStatus(200);
		if (session.getAttribute("user") != null) {
			session.removeAttribute("user");
			response.setMessage("Log Out Successfully!");
		} else {
			response.setMessage("No session to destroy");
		}
		response.print();
	}
}
package Servlet;

import Models.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.Queries;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "user", urlPatterns = {"/user"})
public class User extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Response<Models.User> res = new Response<>();
		Models.User user = Models.User.fromSession(request);
		if (user != null) {
			res.setStatus(200);
			res.setMessage("User Information");
			res.setData(user);
		} else {
			res.setStatus(401);
			res.setMessage("Must Log In");
		}
		ObjectMapper objMapper = new ObjectMapper();
		response.getWriter().print(objMapper.writeValueAsString(res));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String user_email = request.getParameter("userEmail");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		int type_id = Integer.parseInt(request.getParameter("typeId"));

		Response<Models.User> resp = new Response<>();

		Models.User user = new Models.User();
		user.setUserName(name);
		user.setUserEmail(user_email);
		user.setUserUsername(username);
		user.setPassword(password);
		user.setTypeId(type_id);


		if (user.save()) {
			resp.setStatus(200);
			resp.setMessage("User Registered!");
			resp.setData(user);
			user.storeSession(request);
		} else {
			resp.setStatus(400);
			resp.setMessage("This username already exists, please use another one.");
		}
		ObjectMapper objMapper = new ObjectMapper();
		response.getWriter().print(objMapper.writeValueAsString(resp));
	}
}

package Servlet;

import Interfaces.Likeable;
import Interfaces.Model;
import Models.*;
import Models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "Likes", urlPatterns = {"/likes"})
public class LikesServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		switch (req.getParameter("action")) {
			case "like":
				this.liked(req, resp);
				break;
			case "unlike":
				this.unlike(req, resp);
				break;
		}
	}

	private void unlike(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		int id = Integer.parseInt(req.getParameter("id"));
		Likeable model = getObj(req, id);

		Response response = new Response<>();

		if (model.unlikedBy(User.fromSession(req))) {
			response.setStatus(200);
			response.setMessage("Unliked");
		} else {
			response.setStatus(400);
			response.setMessage("Error no unlike was made");
		}

		ObjectMapper objMapper = new ObjectMapper();
		String res = objMapper.writeValueAsString(response);
		resp.getWriter().print(res);
	}

	protected void liked(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int id = Integer.parseInt(req.getParameter("id"));
		Likeable model = getObj(req, id);

		Response response = new Response<>();
		if (model.likedBy(User.fromSession(req))) {
			response.setStatus(200);
			response.setMessage("Liked!");
		} else {
			response.setStatus(400);
			response.setMessage("This is already liked");
		}


		ObjectMapper objMapper = new ObjectMapper();
		String res = objMapper.writeValueAsString(response);
		resp.getWriter().print(res);
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	private Likeable getObj(HttpServletRequest req, int id) {
		switch (req.getParameter("object")) {
			case "manga":
				return Manga.get(id);
			case "chapter":
				return Chapter.get(id);
		}
		return null;
	}
}

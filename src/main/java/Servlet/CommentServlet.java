package Servlet;

import Interfaces.CommentAble;
import Interfaces.CommentModel;
import Interfaces.Likeable;
import Interfaces.Model;
import Models.*;
import Models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.FolderCreator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CommentServlet", urlPatterns = {"/comment"})
public class CommentServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		switch (req.getParameter("action")) {
			case "create":
				this.create(req, resp);
				break;
			case "update":
				this.update(req, resp);
				break;
			case "delete":
				this.delete(req, resp);
				break;
		}

	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		int id = Integer.parseInt(req.getParameter("id"));
		CommentModel model = getObj(req, id);
		Response response = new Response<>();

		if (model.delete()) {
			response.setStatus(200);
			response.setMessage("Comment Deleted!");
		} else {
			response.setStatus(400);
			response.setMessage("Error deleting Comment");
		}

		ObjectMapper objMapper = new ObjectMapper();
		String res = objMapper.writeValueAsString(response);
		resp.getWriter().print(res);

	}

	private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		int id = Integer.parseInt(req.getParameter("id"));
		String comment_content = req.getParameter("comment_content");

		CommentModel model = getObj(req, id);
		model.setComment_content( comment_content );

		Response<CommentModel> response = new Response<>();

		if (model.save()) {
			response.setStatus(200);
			response.setMessage("Comment Updated!");
			response.setData(model);
		} else {
			response.setStatus(400);
			response.setMessage("Error updating Comment");
		}

		ObjectMapper objMapper = new ObjectMapper();
		String res = objMapper.writeValueAsString(response);
		resp.getWriter().print(res);
	}

	private void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		int id = Integer.parseInt(req.getParameter("id"));
		String comment_content = req.getParameter("comment_content");

		CommentModel model = getObj(req, id);
		model.setId( id );
		model.setUser_id( User.fromSession(req).getUserId() );
		model.setComment_content( comment_content );

		Response<CommentModel> response = new Response<>();

		if (model.save()) {
			response.setStatus(200);
			response.setMessage("Comment Created!");
			response.setData(model);
		} else {
			response.setStatus(400);
			response.setMessage("Error Creating Comment");
		}

		ObjectMapper objMapper = new ObjectMapper();
		String res = objMapper.writeValueAsString(response);
		resp.getWriter().print(res);
	}

	private CommentModel getObj(HttpServletRequest req, int id) {
		switch (req.getParameter("object")) {
			case "manga":
				return CommentManga.get(id);
			case "chapter":
				return CommentChapter.get(id);
		}
		return null;
	}

}

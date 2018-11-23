package Servlet;

import AbstractClasses.CommentServlet;
import Interfaces.CommentModel;
import Models.CommentChapter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CommentChapterServlet", urlPatterns = {"/chapter/comment"})
public class CommentChapterServlet extends CommentServlet {
	protected Class<? extends CommentModel> commentClass = CommentChapter.class;

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		super.doDelete(req, resp, commentClass);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		super.doPost(req, resp, commentClass);
	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		super.doPut(req, resp, commentClass);
	}
}

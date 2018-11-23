package Servlet;

import AbstractClasses.CommentServlet;
import Interfaces.CommentModel;
import Models.CommentManga;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CommentMangaServlet", urlPatterns = {"/manga/comment"})
public class CommentMangaServlet extends CommentServlet<CommentManga> {
	protected Class<? extends CommentModel> commentClass = CommentManga.class;


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

package Servlet;

import AbstractClasses.LikeServlet;
import Interfaces.Model;
import Models.LikeChapter;
import Models.LikeManga;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ChapterLike", urlPatterns = {"/chapter/like"})
public class LikeChapterServlet extends LikeServlet<LikeChapter> {
	protected Class<? extends Model> likeClass = LikeChapter.class;
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		super.doDelete(req, resp, likeClass);
	}
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		super.doPost(req, resp, likeClass);
	}
}
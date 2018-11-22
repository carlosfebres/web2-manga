package Servlet;

import Models.*;
import Models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.FolderCreator;
import utils.Queries;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "MangaServlet", urlPatterns = {"/manga"})
public class MangaServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		switch (req.getParameter("action")) {
			case "create":
				this.createManga(req, resp);
				break;
			case "update":
				this.updateManga(req, resp);
				break;
			case "comment":
				this.comment(req, resp);
				break;
			case "get":
				this.get(req, resp);
				break;
		}
	}

	private void comment(HttpServletRequest req, HttpServletResponse resp) {

		String comment_content = req.getParameter("comment_content");
		int manga_id = Integer.parseInt(req.getParameter("manga_id"));

		CommentManga comment = new CommentManga();
		comment.setManga_id( manga_id );
		comment.setUser_id( User.fromSession(req).getUserId() );
		comment.setComment_content( comment_content );

		Response<CommentManga> response = new Response<>();

		if (comment.save()) {
			response.setStatus(200);
			response.setMessage("Comment Created!");
			response.setData(comment);
		} else {
			response.setStatus(400);
			response.setMessage("Error Creating Comment");
		}

		ObjectMapper objMapper = new ObjectMapper();
		String res = null;
		try {
			res = objMapper.writeValueAsString(response);
			resp.getWriter().print(res);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession currentSession = req.getSession();

		int manga_id = Integer.parseInt(req.getParameter("manga_id"));

		Response<Manga> response = new Response<>();

		Manga manga = Manga.get(manga_id);
		manga.isLikedBy( User.fromSession(req) );

		response.setStatus(200);
		response.setMessage("Found!");
		response.setData(manga);

		ObjectMapper objMapper = new ObjectMapper();
		String res = null;
		try {
			res = objMapper.writeValueAsString(response);
			resp.getWriter().print(res);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void createManga(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		String synopsis = req.getParameter("synopsis");
		int status = Integer.parseInt(req.getParameter("status"));
		String genres = req.getParameter("genres"); // 1,2,3,4

		FolderCreator folderCreator = new FolderCreator();

		Manga manga = new Manga();
		manga.setUserId(User.fromSession(req).getUserId());
		manga.setMangaName(name);
		manga.setMangaSynopsis(synopsis);
		manga.setMangaStatus(status);
		manga.clear_genres();
		manga.set_genres(genres);

		Response<Manga> response = new Response<>();

		if (manga.save()) {
			boolean folderCreation = folderCreator.createMangaFolder(manga.getMangaName());
			response.setStatus(200);
			response.setMessage("Manga Created!");
			response.setData(manga);
		} else {
			response.setStatus(400);
			response.setMessage("Error Creating Manga");
		}

		ObjectMapper objMapper = new ObjectMapper();
		String res = objMapper.writeValueAsString(response);
		resp.getWriter().print(res);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int manga_id = Integer.parseInt(req.getParameter("manga_id"));
		Queries queryExecutor = new Queries();
		boolean deleted = queryExecutor.deleteManga(manga_id);

		Response response = new Response();
		if (deleted) {
			response.setStatus(200);
			response.setMessage("Manga Deleted!");
		} else {
			response.setStatus(400);
			response.setMessage("Error Deleting Manga");
		}
		ObjectMapper objMapper = new ObjectMapper();
		String res = objMapper.writeValueAsString(response);
		resp.getWriter().print(res);
	}

	protected void updateManga(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession currentSession = req.getSession();

		int status = Integer.parseInt(req.getParameter("status"));
		int manga_id = Integer.parseInt(req.getParameter("manga_id"));
		String name = req.getParameter("name");
		String synopsis = req.getParameter("synopsis");
		String genres = req.getParameter("genres"); // 1,2,3,4

		FolderCreator folderCreator = new FolderCreator();

		Manga manga = Manga.get(manga_id);
		String old_manga_name = manga.getMangaName();
		manga.setMangaName(name);
		manga.setMangaSynopsis(synopsis);
		manga.setMangaStatus(status);
		manga.clear_genres();
		manga.set_genres(genres);

		Response<Manga> response = new Response<>();

		if (manga.save()) {
			if (!old_manga_name.equals(manga.getMangaName())) {
				folderCreator.renameManga(old_manga_name, manga.getMangaName());
			}
			response.setStatus(200);
			response.setMessage("Manga Updated!");
			response.setData(manga);
		} else {
			response.setStatus(400);
			response.setMessage("Error Updating Manga");
		}
		ObjectMapper objMapper = new ObjectMapper();
		String res = objMapper.writeValueAsString(response);
		resp.getWriter().print(res);

	}
}

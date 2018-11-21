package Servlet;

import Models.Response;
import Models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.FolderCreator;
import utils.Queries;

import Models.Manga;

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
		switch( req.getParameter("action") ) {
			case "create":
				this.createManga(req, resp);
				break;
			case "update":
				this.updateManga(req, resp);
				break;
			case "liked":
				this.liked(req, resp);
				break;
		}
	}

	protected void createManga(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession currentSession = req.getSession();

		String name = req.getParameter("name");
		String synopsis = req.getParameter("synopsis");
		int status = Integer.parseInt( req.getParameter("status") );
		String genres = req.getParameter("genres"); // 1,2,3,4

		FolderCreator folderCreator = new FolderCreator();

		Manga manga = new Manga();
		manga.setUser_id(User.fromSession(req).getUser_id());
		manga.setManga_name(name);
		manga.setManga_synopsis(synopsis);
		manga.setManga_status(status);
		manga.clear_genres();
		manga.set_genres(genres);

		Response<Manga> response = new Response<>();

		if (manga.save()) {
			boolean folderCreation = folderCreator.createMangaFolder(manga.getManga_name());
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

	protected void liked(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession currentSession = req.getSession();
		int manga_id = Integer.parseInt( req.getParameter("manga_id") );

		Manga manga = Manga.get(manga_id);

		Response response = new Response<>();

		if (manga.likedBy(User.fromSession(req))) {
			response.setStatus(200);
			response.setMessage("Manga Liked!");
		} else {
			response.setStatus(400);
			response.setMessage("Error, this manga was not liked");
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

		int status = Integer.parseInt( req.getParameter("status") );
		int manga_id = Integer.parseInt( req.getParameter("manga_id") );
		String name = req.getParameter("name");
		String synopsis = req.getParameter("synopsis");
		String genres = req.getParameter("genres"); // 1,2,3,4

		FolderCreator folderCreator = new FolderCreator();

		Manga manga = Manga.get(manga_id);
		String old_manga_name = manga.getManga_name();
		manga.setManga_name(name);
		manga.setManga_synopsis(synopsis);
		manga.setManga_status(status);
		manga.clear_genres();
		manga.set_genres(genres);

		Response<Manga> response = new Response<>();

		if (manga.save()) {
			if ( ! old_manga_name.equals( manga.getManga_name() ) ) {
				folderCreator.renameManga(old_manga_name, manga.getManga_name());
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

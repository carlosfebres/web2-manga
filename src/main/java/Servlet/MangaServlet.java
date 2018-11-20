package Servlet;

import Models.Response;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.FolderCreator;
import helpers.RequestMapper;
import utils.Queries;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@WebServlet(name = "MangaServlet", urlPatterns = {"/manga"})
public class MangaServlet extends HttpServlet {

	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession currentSession = req.getSession();
		RequestMapper mapper = new RequestMapper();
		HashMap<String, Object> bodyMap = mapper.payloadToMap(req);
		int user_id = Integer.parseInt((String) currentSession.getAttribute("user_id"));
		String manga_name = (String) bodyMap.get("manga_name");
		String manga_genre = (String) bodyMap.get("manga_genre");
		String manga_synopsis = (String) bodyMap.get("manga_synopsis");
		int manga_status = (int) bodyMap.get("manga_status");
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

		Queries queryExecutor = new Queries();
		FolderCreator folderCreator = new FolderCreator();
		Boolean success =
				queryExecutor.createManga(user_id, manga_genre, manga_name, manga_status, manga_synopsis, timestamp);

		if (success) {
			System.out.println("success creating manga in database");
			boolean folderCreation = folderCreator.createMangaFolder(manga_genre, manga_name);
			if (folderCreation) {
				System.out.println("FolderCreation succeeded");
				resp.sendRedirect("index.html");
			} else {
				System.out.println("Folder creation failed");
				resp.sendError(400);
			}
		} else {
			resp.sendError(400);
		}


	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int manga_id = Integer.parseInt(req.getParameter("manga_id"));
		Queries queryExecutor = new Queries();
		boolean deleted = queryExecutor.deleteManga(manga_id);
		if (deleted) {
			resp.sendRedirect("index.html");

		} else {
			resp.sendError(400);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession currentSession = req.getSession();

		String new_synopsis = (String) req.getAttribute("new_synopsis");
		int manga_id = (int) req.getAttribute("manga_id");
		String new_name = (String) req.getAttribute("new_name");
		String new_genre = (String) req.getAttribute("new_genre");

		Queries queryExecutor = new Queries();
		FolderCreator folderCreator = new FolderCreator();
		HashMap<String, String> dbData = queryExecutor.getMangaData(manga_id);
		boolean dbSuccess = queryExecutor.updateManga(dbData.get("manga_name"), dbData.get("manga_genre"), Integer.parseInt(dbData.get("genres_id")), manga_id, new_genre, new_name, new_synopsis);

		Response response = new Response();

		if (dbSuccess) {
			folderCreator.renameManga(dbData.get("manga_name"), dbData.get("manga_genre"), new_name, new_genre);
			response.setStatus(200);
			response.setMessage("Manga Updated!");
		} else {
			response.setStatus(400);
			response.setMessage("Error Updating Manga");
		}

	}
}

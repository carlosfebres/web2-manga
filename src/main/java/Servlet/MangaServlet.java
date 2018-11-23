package Servlet;

import Exceptions.ModelNotFound;
import CustomHelpers.RequestMapper;
import Models.Manga;
import Models.Response;
import Models.User;
import CustomHelpers.FolderCreator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "MangaServlet", urlPatterns = {"/manga"})
public class MangaServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Manga manga = (Manga) RequestMapper.toModel(req, Manga.class);
		Response<Manga> response = new Response<>(resp);
		try {
			manga.fetch();
			manga.isLikedBy( User.fromSession(req) );

			response.setStatus(200);
			response.setMessage("Found!");
			response.setData(manga);
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
			response.setStatus(404);
			response.setMessage("Not Found!");
		} finally {
			response.print();
		}
	}


	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Manga manga = (Manga) RequestMapper.toModel(req, Manga.class);
		manga.setUserId(User.fromSession(req).getUserId());
		Response<Manga> response = new Response<>(resp);
		FolderCreator folderCreator = new FolderCreator();

		if (manga.save()) {
			folderCreator.createMangaFolder(manga.getMangaName());
			response.setStatus(200);
			response.setMessage("Manga Created!");
			response.setData(manga);
		} else {
			response.setStatus(400);
			response.setMessage("Error Creating Manga");
		}
		response.print();
	}


	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Manga manga = (Manga) RequestMapper.toModel(req, Manga.class);
		Response response = new Response<>(resp);
		try {
			manga.fetch();
			if (manga.delete()) {
				response.setStatus(200);
				response.setMessage("Manga Deleted!");
			} else {
				response.setStatus(400);
				response.setMessage("Error Deleting Manga");
			}
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
			response.setStatus(404);
			response.setMessage("Manga Not Found");
		} finally {
			response.print();
		}
	}


	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Manga manga = (Manga) RequestMapper.toModel(req, Manga.class);
		manga.setUserId(User.fromSession(req).getUserId());
		Response<Manga> response = new Response<>(resp);
		FolderCreator folderCreator = new FolderCreator();
		try {
			Manga old_manga = Manga.get(manga.getMangaId());
			if (manga.save()) {
				if (!old_manga.getMangaName().equals(manga.getMangaName())) {
					folderCreator.renameManga(old_manga.getMangaName(), manga.getMangaName());
				}
				response.setStatus(200);
				response.setMessage("Manga Updated!");
				response.setData(manga);
			} else {
				response.setStatus(400);
				response.setMessage("Error Updating Manga");
			}
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
			response.setStatus(404);
			response.setMessage("Manga Not Found");
		} finally {
			response.print();
		}
	}
}

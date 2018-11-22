package Servlet;

import Models.*;
import Models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.FolderCreator;
import utils.Queries;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;


@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
		maxFileSize = 1024 * 1024 * 10,      // 10MB
		maxRequestSize = 1024 * 1024 * 50)
@WebServlet(name = "ChapterServlet", urlPatterns = {"/chapter"})
public class ChapterServlet extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		switch (req.getParameter("action")) {
			case "create":
				this.create(req, resp);
				break;
			case "get":
				this.get(req, resp);
				break;
			case "comment":
				this.comment(req, resp);
				break;
		}
	}

	private void comment(HttpServletRequest req, HttpServletResponse resp) {

		String comment_content = req.getParameter("comment_content");
		int chapter_id = Integer.parseInt(req.getParameter("chapter_id"));

		CommentChapter comment = new CommentChapter();
		comment.setChapter_id( chapter_id );
		comment.setUser_id( User.fromSession(req).getUserId() );
		comment.setComment_content( comment_content );

		Response<CommentChapter> response = new Response<>();

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

		int chapter_id = Integer.parseInt( req.getParameter("chapter_id") );

		Response<Chapter> response = new Response<>();

		response.setStatus(200);
		response.setMessage("Found!");
		response.setData( Chapter.get(chapter_id) );

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


	protected void create(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession currentSession = req.getSession();

		int manga_id = Integer.parseInt(req.getParameter("manga_id"));
		int chapter_number = Integer.parseInt(req.getParameter("chapter_number"));
		int chapter_num_pages = Integer.parseInt(req.getParameter("chapter_num_pages"));
		String chapter_title = req.getParameter("chapter_title");

		FolderCreator folderCreator = new FolderCreator();
		Manga manga = Manga.get(manga_id);

		Part chapterFilePart = req.getPart("chapter_file");

		InputStream filecontent = chapterFilePart.getInputStream();
		OutputStream os = null;
		String path = "";
		try {
			path = folderCreator.createChapterFolder(manga.getMangaName(), chapter_title);
			System.out.println(path + "/" + getFileName(chapterFilePart));
			os = new FileOutputStream(path + "/" + getFileName(chapterFilePart));
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (filecontent != null) {
				filecontent.close();
			}
			if (os != null) {
				os.close();
			}

			Chapter chapter = new Chapter();
			chapter.setManga_id(manga_id);
			chapter.setChapter_number(chapter_number);
			chapter.setChapter_num_pages(chapter_num_pages);
			chapter.setChapter_title(chapter_title);
			System.out.println(getFileName(chapterFilePart));
			chapter.setChapter_location(path + "/" + getFileName(chapterFilePart));

			Response<Chapter> response = new Response<>();

			if (chapter.save()) {
				response.setStatus(200);
				response.setMessage("Chapter Created!");
				response.setData(chapter);
			} else {
				response.setStatus(400);
				response.setMessage("Error Creating Chapter");
			}

			ObjectMapper objMapper = new ObjectMapper();
			String res = objMapper.writeValueAsString(response);
			resp.getWriter().print(res);
		}


	}

	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int chapter_id = Integer.parseInt(req.getParameter("chapter_id"));
		int chapter_number = Integer.parseInt(req.getParameter("chapter_number"));
		String manga_name = req.getParameter("manga_name");
		String manga_genre = req.getParameter("manga_genre");

		Queries queryExecutor = new Queries();
		FolderCreator folderCreator = new FolderCreator();
		boolean dbSuccess = queryExecutor.deleteChapter(chapter_id);
		if (dbSuccess) {
			System.out.println("deleted from dstabase");
			boolean deleteFolder = folderCreator.deleteChapter(chapter_id, chapter_number, manga_genre, manga_name);
			if (deleteFolder) {
				System.out.println("deleted folder");
				resp.sendRedirect("index.html");
			} else {
				resp.sendError(400);
			}
		} else {
			resp.sendError(400);
		}
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession currentSession = req.getSession();
		Part chapterDataPart = req.getPart("chapter_data");
		Scanner s = new Scanner(chapterDataPart.getInputStream());
		String chapter_data = s.nextLine();
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> requestJsonMap = mapper.readValue(chapter_data, HashMap.class);
		System.out.println(requestJsonMap.toString());
		String chapter_title = (String) requestJsonMap.get("chapter_title");
		int chapter_number = (int) requestJsonMap.get("chapter_number");
		int chapter_num_pages = (int) requestJsonMap.get("chapter_num_pages");
		int manga_id = (int) requestJsonMap.get("manga_id");
		String chapter_location = (String) requestJsonMap.get("chapter_location");
		String manga_name = (String) requestJsonMap.get("manga_name");
		String manga_genre = (String) requestJsonMap.get("manga_genre");

		Queries queryExecutor = new Queries();
		boolean dbSuccess = queryExecutor.addChapter(chapter_title, chapter_number, chapter_num_pages, chapter_location, manga_genre, manga_id, manga_name);
		if (dbSuccess) {
			Part chapterFilePart = req.getPart("chapter_file");

			InputStream filecontent = chapterFilePart.getInputStream();
			OutputStream os = null;
			try {
				FolderCreator folderCreator = new FolderCreator();
				String path = folderCreator.createChapterFolder(manga_name, chapter_title);
				os = new FileOutputStream(path + "/" + getFileName(chapterFilePart));
				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = filecontent.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (filecontent != null) {
					filecontent.close();
				}
				if (os != null) {
					os.close();
				}
				System.out.println("created file ");
				resp.sendRedirect("index.html");
			}
		} else {
			resp.sendError(400);
		}


	}

	// Esta funcion permite obtener el nombre del archivo
	private String getFileName(Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
}

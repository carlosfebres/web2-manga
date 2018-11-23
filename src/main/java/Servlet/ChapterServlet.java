package Servlet;

import Exceptions.ModelNotFound;
import CustomHelpers.FileHandler;
import CustomHelpers.RequestMapper;
import Models.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import CustomHelpers.FolderCreator;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;


@MultipartConfig(fileSizeThreshold = 2097152 /*2MB*/, maxFileSize = 10485760 /*10MB*/, maxRequestSize = 52428800/*50MB*/)
@WebServlet(name = "ChapterServlet", urlPatterns = {"/chapter"})
public class ChapterServlet extends HttpServlet {


	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		Response<Chapter> response = new Response<>(resp);

		try {
			Scanner scanner = new Scanner(req.getPart("chapterId").getInputStream());
			String chapterId = scanner.next();

			Chapter chapter = Chapter.get( chapterId );
			Tracker.getFor( chapter.getMangaId(), User.fromSession(req).getUserId() ).addChapter(chapter);

			response.setStatus(200);
			response.setMessage("Found!");
			response.setData(chapter);
		} catch (ModelNotFound modelNotFound) {
			response.setStatus(404);
			response.setMessage("Chapter Not Found");
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			response.print();
		}
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Part chapterDataPart = req.getPart("chapter_data");
		Scanner s = new Scanner(chapterDataPart.getInputStream());
		String chapter_data = s.nextLine();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Chapter chapter = mapper.readValue(chapter_data, Chapter.class);

		FolderCreator folderCreator = new FolderCreator();
		Part chapterFilePart = req.getPart("chapter_file");

		Manga manga = null;
		Response<Chapter> response = new Response<>(resp);
		try {
			manga = Manga.get(chapter.getMangaId());
			String path = folderCreator.createChapterFolder(manga.getMangaName(), chapter.getChapterTitle());
			String filePath = FileHandler.download(chapterFilePart, path);
			chapter.setChapterLocation(filePath);
			if (chapter.save()) {
				response.setStatus(200);
				response.setMessage("Chapter Created!");
				response.setData(chapter);
			} else {
				response.setStatus(400);
				response.setMessage("Error Creating Chapter");
			}
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
		} finally {
			response.print();
		}
	}


	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Scanner scanner = new Scanner(req.getPart("chapterId").getInputStream());
		String chapterId = scanner.next();

		Response response = new Response<>(resp);
		try {
			Chapter chapter = Chapter.get(chapterId);
			if (chapter.delete()) {
				response.setStatus(200);
				response.setMessage("Chapter Deleted!");
			} else {
				response.setStatus(400);
				response.setMessage("Error Deleting Chapter");
			}
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
			response.setStatus(404);
			response.setMessage("Chapter Not Found");
		} finally {
			response.print();
		}
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Part chapterDataPart = req.getPart("chapter_data");
		Scanner s = new Scanner(chapterDataPart.getInputStream());
		String chapter_data = s.nextLine();

		Response<Chapter> response = new Response<>(resp);
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Chapter chapter = mapper.readValue(chapter_data, Chapter.class);

			Chapter old_chapter = Chapter.get(chapter.getChapterId());
			chapter.setChapterLocation( old_chapter.getChapterLocation() );
			if (chapter.save()) {
				response.setStatus(200);
				response.setMessage("Chapter Updated!");
				response.setData(chapter);
			} else {
				response.setStatus(400);
				response.setMessage("Error Updating Chapter");
			}
		} catch (ModelNotFound modelNotFound) {
			modelNotFound.printStackTrace();
			response.setStatus(404);
			response.setMessage("Chapter Not Found");
		} finally {
			response.print();
		}
	}

}

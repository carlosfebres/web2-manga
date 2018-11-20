package Servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.FolderCreator;
import helpers.RequestMapper;
import utils.Queries;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;


@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
        maxFileSize=1024*1024*10,      // 10MB
        maxRequestSize=1024*1024*50)
@WebServlet(name = "ChapterServlet", urlPatterns = {"/chapter"})
public class ChapterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        HttpSession currentSession = req.getSession();
        RequestMapper mapper = new RequestMapper();
        HashMap requestJsonMap = mapper.payloadToMap(req);
        System.out.println(requestJsonMap.toString());
        String chapter_title = (String) requestJsonMap.get("chapter_title");
        int chapter_number= (int) requestJsonMap.get("chapter_number");
        String manga_name = (String) requestJsonMap.get("manga_name");
        String manga_genre = (String) requestJsonMap.get("manga_genre");
        FolderCreator folderCreator = new FolderCreator();
        String path = folderCreator.createChapterFolder(manga_genre,manga_name,chapter_number,chapter_title);


        // Find this file id in database to get file name, and file type

        // You must tell the browser the file type you are going to send
        // for example application/pdf, text/plain, text/html, image/jpg
        response.setContentType("image/jpg");

        // Make sure to show the download dialog
        response.setHeader("Content-disposition","attachment; filename=slack.jpg");
        System.out.println("the directory is: "+path);
        System.out.println(new File(path).list()[0]);
        File my_file = new File(path+"/"+new File(path).list()[0]);

        // This should send the file to browser
        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(my_file);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0){
            out.write(buffer, 0, length);
        }
        in.close();
        out.flush();

    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int chapter_id = Integer.parseInt(req.getParameter("chapter_id"));
        int chapter_number = Integer.parseInt(req.getParameter("chapter_number"));
        String manga_name = req.getParameter("manga_name");
        String manga_genre = req.getParameter("manga_genre");

        Queries queryExecutor = new Queries();
        FolderCreator folderCreator = new FolderCreator();
        boolean dbSuccess = queryExecutor.deleteChapter(chapter_id);
        if(dbSuccess){
            System.out.println("deleted from dstabase");
            boolean deleteFolder = folderCreator.deleteChapter(chapter_id,chapter_number,manga_genre,manga_name);
            if(deleteFolder){
                System.out.println("deleted folder");
                resp.sendRedirect("index.html");
            } else {
                resp.sendError(400);
            }
        } else{
            resp.sendError(400);
        }
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession currentSession = req.getSession();
        Part chapterDataPart = req.getPart("chapter_data");
        Scanner s = new Scanner(chapterDataPart.getInputStream());
        String chapter_data = s.nextLine();
        ObjectMapper mapper = new ObjectMapper();
        HashMap <String,Object> requestJsonMap = mapper.readValue(chapter_data, HashMap.class);
        System.out.println(requestJsonMap.toString());
        String chapter_title = (String) requestJsonMap.get("chapter_title");
        int chapter_number= (int) requestJsonMap.get("chapter_number");
        int chapter_num_pages= (int) requestJsonMap.get("chapter_num_pages");
        int manga_id = (int) requestJsonMap.get("manga_id");
        String chapter_location = (String) requestJsonMap.get("chapter_location");
        String manga_name = (String) requestJsonMap.get("manga_name");
        String manga_genre = (String) requestJsonMap.get("manga_genre");

        Queries queryExecutor = new Queries();
        boolean dbSuccess = queryExecutor.addChapter(chapter_title,chapter_number,chapter_num_pages,chapter_location,manga_genre,manga_id,manga_name);
        if(dbSuccess) {
            Part chapterFilePart = req.getPart("chapter_file");

            InputStream filecontent = chapterFilePart.getInputStream();
            OutputStream os = null;
            try {
                FolderCreator  folderCreator = new FolderCreator();
                String path = folderCreator.createChapterFolder(manga_genre,manga_name,chapter_number,chapter_title);
                os = new FileOutputStream(path+"/"+getFileName(chapterFilePart));
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

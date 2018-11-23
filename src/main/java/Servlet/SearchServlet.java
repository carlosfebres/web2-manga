package Servlet;

import CustomHelpers.RequestMapper;
import Models.Response;
import Models.SearchMangas;
import Models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SearchServlet", urlPatterns = {"/manga/search"})
public class SearchServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SearchMangas search = (SearchMangas) RequestMapper.toObject(request, SearchMangas.class);
		Response<SearchMangas> resp = new Response<>(response);
		search.search();
		resp.setStatus(200);
		resp.setMessage("Result");
		resp.setData(search);
		resp.print();
	}
}

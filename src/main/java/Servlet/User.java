package Servlet;

import Models.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.Queries;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "user", urlPatterns = {"/user"})
public class User extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Response<Models.User> res = new Response<>();
        Models.User user = Models.User.fromSession(request);
        if (user != null) {
            res.setStatus(200);
            res.setMessage("User Information");
            res.setData(user);
        } else {
            res.setStatus(401);
            res.setMessage("Must Log In");
        }
        ObjectMapper objMapper = new ObjectMapper();
        response.getWriter().print( objMapper.writeValueAsString(res) );
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String last_name = request.getParameter("last_name");
        String user_email = request.getParameter("user_email");
        String username = request.getParameter("user");
        String password = request.getParameter("password");
        int type_id = Integer.parseInt( request.getParameter("type_id") );

        Queries co = new Queries();
        Response<Models.User> resp = new Response<>();
        Models.User user = co.registry(name, last_name, user_email, username, password, type_id);
        if (user != null) {
            resp.setStatus(200);
            resp.setMessage("User Registered!");
            resp.setData( user );
            user.storeSession(request);
        } else {
            resp.setStatus(400);
            resp.setMessage("This username already exists, please use another one.");
        }
        ObjectMapper objMapper = new ObjectMapper();
        response.getWriter().print( objMapper.writeValueAsString(resp) );
    }
}

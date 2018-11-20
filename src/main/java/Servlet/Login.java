package Servlet;

import Models.Response;
import Models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.Queries;
//import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet( name = "login", urlPatterns = "/Login" )
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("user");
        String password = request.getParameter("password");

        Queries co = new Queries();
        User user = co.authentication(username, password);

        Response<User> resp = new Response<>();
        ObjectMapper objMapper = new ObjectMapper();

        if (user != null) {
            resp.setMessage("User Logged!");
            resp.setStatus(200);
            resp.setData(user);
            System.out.println("logged in user with id: " + user.user_id);
            user.storeSession(request);
        } else {
            resp.setMessage("Authentication Failed");
            resp.setStatus(401);
        }
        String res = objMapper.writeValueAsString(resp);
        response.getWriter().print(res);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Login Servlet");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response response = new Response();
        HttpSession session = req.getSession();
        response.setStatus(200);
        if(session.getAttribute("user") != null){
            session.removeAttribute("user");
            response.setMessage("Log Out Successfully!");
        } else {
            response.setMessage("No session to destroy");
        }
        ObjectMapper objMapper = new ObjectMapper();
        String res = objMapper.writeValueAsString(response);
        resp.getWriter().print(res);
    }
}
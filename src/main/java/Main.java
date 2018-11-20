import Servlet.*;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

public class Main {

/*
    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();
        Integer port = 9000;
        String web_app = "src/main/webapp";
        tomcat.setPort(port);
        Context ctx = null;
        Connector connector = tomcat.getConnector();
        connector.setURIEncoding("UTF-8");


        ctx = tomcat.addWebapp("/", System.getProperty("user.dir") + "\\" + web_app);
        ctx.setAllowCasualMultipartParsing(true);

        Tomcat.addServlet(ctx, "LoginServlet", new Login());
        ctx.addServletMappingDecoded("/login", "LoginServlet");

        Tomcat.addServlet(ctx, "RegistryServlet", new Registry());
        ctx.addServletMappingDecoded("/registry", "RegistryServlet");

        Tomcat.addServlet(ctx, "LogoutServlet", new Logout());
        ctx.addServletMappingDecoded("/logout", "LogoutServlet");

        Tomcat.addServlet(ctx, "UserServlet", new User());
        ctx.addServletMappingDecoded("/user", "UserServlet");

        Tomcat.addServlet(ctx, "MangaServlet", new MangaServlet());
        ctx.addServletMappingDecoded("/manga", "MangaServlet");

        Tomcat.addServlet(ctx, "ChapterServlet", new ChapterServlet());
        ctx.addServletMappingDecoded("/chapter", "ChapterServlet");


        System.out.println(123);


        tomcat.start();
        tomcat.getServer().await();
    }
    */
}
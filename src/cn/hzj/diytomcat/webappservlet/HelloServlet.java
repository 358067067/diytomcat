package cn.hzj.diytomcat.webappservlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            resp.getWriter().println("Hello DIY Tomcat from HelloServlet");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package cn.how2j.diytomcat.servlets;

import cn.how2j.diytomcat.catalina.Context;
import cn.how2j.diytomcat.http.Request;
import cn.how2j.diytomcat.http.Response;
import cn.how2j.diytomcat.util.Constant;
import cn.hutool.core.util.ReflectUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InvokerServlet {

    private static InvokerServlet instance = new InvokerServlet();

    public static InvokerServlet getInstance() {
        return instance;
    }

    private InvokerServlet() {

    }

    public void service(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) throws IOException, ServletException {
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;

        String uri = request.getUri();
        Context context = request.getContext();
        String ServletClassName = context.getServletClassName(uri);

        Object ServletObject = ReflectUtil.newInstance(ServletClassName);
        ReflectUtil.invoke(ServletObject, "service", request, response);
        response.setStatus(Constant.CODE_200);
    }
}

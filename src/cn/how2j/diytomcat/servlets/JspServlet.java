package cn.how2j.diytomcat.servlets;

import cn.how2j.diytomcat.http.Request;
import cn.how2j.diytomcat.http.Response;
import cn.how2j.diytomcat.util.Constant;
import cn.how2j.diytomcat.util.WebXMLUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;

public class JspServlet extends HttpServlet {

    private static JspServlet jspServlet = new JspServlet();

    public static synchronized JspServlet getInstance() {
        return jspServlet;
    }

    private JspServlet() {

    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        try {
            Request request = (Request) req;
            Response response = (Response) res;

            String uri = request.getUri();

            if ("/".equals(uri))
                uri = WebXMLUtil.getWelcomeFile(request.getContext());
            String fileName = StrUtil.removePrefix(uri, "/");
            File file = FileUtil.file(request.getRealPath(fileName));

            if (file.exists()) {
                String extName = FileUtil.extName(file);
                String mimeType = WebXMLUtil.getMimeType(extName);
                response.setContentType(mimeType);

                byte body[] = FileUtil.readBytes(file);
                response.setBody(body);

                response.setStatus(Constant.CODE_200);
            } else {
                response.setStatus(Constant.CODE_404);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

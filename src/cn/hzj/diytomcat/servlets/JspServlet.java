package cn.hzj.diytomcat.servlets;

import cn.hzj.diytomcat.classloader.JspClassLoader;
import cn.hzj.diytomcat.util.Constant;
import cn.hzj.diytomcat.util.JspUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hzj.diytomcat.catalina.Context;
import cn.hzj.diytomcat.http.Request;
import cn.hzj.diytomcat.http.Response;
import cn.hzj.diytomcat.util.WebXMLUtil;

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
            cn.hzj.diytomcat.http.Request request = (Request) req;
            cn.hzj.diytomcat.http.Response response = (Response) res;

            String uri = request.getUri();

            if ("/".equals(uri))
                uri = cn.hzj.diytomcat.util.WebXMLUtil.getWelcomeFile(request.getContext());

            String fileName = StrUtil.removePrefix(uri, "/");
            File file = FileUtil.file(request.getRealPath(fileName));
            // jsp文件是否存在
            File jspFile = file;
            if (jspFile.exists()) {
                Context context = request.getContext();
                String path = context.getPath();
                String subFolder;
                if ("/".equals(path))
                    subFolder = "_";
                else
                    subFolder = StrUtil.subAfter(path, "/", false);
                String servletClassPath = JspUtil.getServletClassPath(uri, subFolder);
                // class文件是否存在
                File jspServletClassFile = new File(servletClassPath);
                if (jspServletClassFile.exists()) {
                    JspUtil.compileJsp(context, jspFile);
                } else if (jspFile.lastModified() > jspServletClassFile.lastModified()) {// 判断jsp文件和class文件的日期
                    JspUtil.compileJsp(context, jspFile);
                    JspClassLoader.invalidJspClassLoader(uri, context);
                }

                String extName = FileUtil.extName(file);
                String mimeType = WebXMLUtil.getMimeType(extName);
                response.setContentType(mimeType);

                JspClassLoader jspClassLoader = JspClassLoader.getJspClassLoader(uri, context);
                String jspServletClassName = JspUtil.getJspServletClassName(uri, subFolder);
                Class jspServletClass = jspClassLoader.loadClass(jspServletClassName);

                HttpServlet servlet = context.getServlet(jspServletClass);
                servlet.service(req, res);
                if (null == response.getRedirectPath())
                    response.setStatus(Constant.CODE_200);
                else
                    response.setStatus(Constant.CODE_302);
            } else {
                response.setStatus(Constant.CODE_404);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

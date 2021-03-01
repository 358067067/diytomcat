package cn.how2j.diytomcat.classloader;

import cn.how2j.diytomcat.catalina.Context;
import cn.how2j.diytomcat.util.Constant;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class JspClassLoader extends URLClassLoader {

    private static Map<String, JspClassLoader> map = new HashMap<>();

    // 让过期的JSP与JspClassLoader取消关联
    public static void invalidJspClassLoader(String url, Context context) {
        String key = context.getPath() + "/" + url;
        map.remove(key);
    }

    // 获取JSP对应的JspClassLoader
    public static JspClassLoader getJspClassLoader(String uri, Context context) {
        String key = context.getPath() + "/" + uri;
        JspClassLoader loader = map.get(key);
        if (null == loader) {
            loader = new JspClassLoader(context);
            map.put(key, loader);
        }
        return loader;
    }

    public JspClassLoader(Context context) {
        super(new URL[]{}, context.getWebappClassLoader());
        try {
            String subFolder;
            String path = context.getPath();
            if ("/".equals(path))
                subFolder = "_";
            else
                subFolder = StrUtil.subAfter(path, "/", false);
            File classFolder = new File(Constant.workFolder, subFolder);
            URL url = new URL("file:" + classFolder.getAbsolutePath() + "/");
            this.addURL(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

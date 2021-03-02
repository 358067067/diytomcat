package cn.hzj.diytomcat.util;

import cn.hzj.diytomcat.catalina.Connector;
import cn.hzj.diytomcat.catalina.Context;
import cn.hzj.diytomcat.catalina.Service;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebXMLUtil {

    private static Map<String, String> mimeTypeMapping = new HashMap<>();

    public static String getWelcomeFile(Context context) {
        String xml = FileUtil.readUtf8String(cn.hzj.diytomcat.util.Constant.webXmlFile);
        Document doc = Jsoup.parse(xml);
        Elements es = doc.select("welcome-file");
        for (Element e : es) {
            String welcomeFileName = e.text();
            File f = new File(context.getDocBase(), welcomeFileName);
            if (f.exists())
                return f.getName();
        }
        return "index.html";
    }

    private static void initMimeType() {
        String xml = FileUtil.readUtf8String(cn.hzj.diytomcat.util.Constant.webXmlFile);
        Document doc = Jsoup.parse(xml);
        Elements es = doc.select("mime-mapping");
        es.forEach(e -> {
            mimeTypeMapping.put(e.select("extension").first().text(), e.select("mime-type").first().text());
        });
    }

    public static synchronized String getMimeType(String extName) {
        if (mimeTypeMapping.isEmpty())
            initMimeType();
        String mimeType = mimeTypeMapping.get(extName);
        if (null == mimeType)
            return "text/html";
        return mimeType;
    }

    public static List<Connector> getConnectors(Service service) {
        List<Connector> result = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("Connector");
        es.forEach(e -> {
            int port = Convert.toInt(e.attr("port"));
            Connector c = new Connector(service);
            c.setPort(port);
            result.add(c);
        });
        return result;
    }
}

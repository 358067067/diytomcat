package cn.hzj.diytomcat.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hzj.diytomcat.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ServerXMLUtil {

    public static List<cn.hzj.diytomcat.catalina.Context> getContexts(cn.hzj.diytomcat.catalina.Host host) {
        List<cn.hzj.diytomcat.catalina.Context> result = new ArrayList<>();

        String xml = FileUtil.readUtf8String(cn.hzj.diytomcat.util.Constant.serverXmlFile);

        Document doc = Jsoup.parse(xml);

        Elements es = doc.select("Context");

        es.forEach(e -> {
            boolean reloadable = Convert.toBool(e.attr("reloadable"), true);
            cn.hzj.diytomcat.catalina.Context context = new Context(e.attr("path"), e.attr("docBase"), host, reloadable);
            result.add(context);
        });
        return result;
    }

    public static String getServiceName() {
        String xml = FileUtil.readUtf8String(cn.hzj.diytomcat.util.Constant.serverXmlFile);
        Document doc = Jsoup.parse(xml);
        Element host = doc.select("service").first();
        return host.attr("name");
    }

    public static List<cn.hzj.diytomcat.catalina.Host> getHosts(cn.hzj.diytomcat.catalina.Engine engine) {
        List<cn.hzj.diytomcat.catalina.Host> result = new ArrayList<>();
        String xml = FileUtil.readUtf8String(cn.hzj.diytomcat.util.Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Elements es = d.select("Host");
        es.forEach(e -> {
            cn.hzj.diytomcat.catalina.Host host = new cn.hzj.diytomcat.catalina.Host(e.attr("name"), engine);
            result.add(host);
        });
        return result;
    }

    public static String getEngineDefaultHost() {
        String xml = FileUtil.readUtf8String(cn.hzj.diytomcat.util.Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Element host = d.select("Engine").first();
        return host.attr("defaultHost");
    }

    public static List<cn.hzj.diytomcat.catalina.Connector> getConnectors(cn.hzj.diytomcat.catalina.Service service) {
        List<cn.hzj.diytomcat.catalina.Connector> result = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("Connector");
        es.forEach(e -> {
            int port = Convert.toInt(e.attr("port"));
            String compression = e.attr("compression");
            int compressionMinSize = Convert.toInt(e.attr("compressionMinSize"), 0);
            String noCompressionUserAgents = e.attr("noCompressionUserAgents");
            String compressableMimeType = e.attr("compressableMimeType");

            cn.hzj.diytomcat.catalina.Connector c = new cn.hzj.diytomcat.catalina.Connector(service);
            c.setPort(port);
            c.setCompression(compression);
            c.setCompressionMinSize(compressionMinSize);
            c.setNoCompressionUserAgents(noCompressionUserAgents);
            c.setCompressableMimeType(compressableMimeType);
            result.add(c);
        });
        return result;
    }
}

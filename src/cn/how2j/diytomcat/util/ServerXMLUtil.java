package cn.how2j.diytomcat.util;

import cn.how2j.diytomcat.catalina.*;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ServerXMLUtil {

    public static List<Context> getContexts(Host host) {
        List<Context> result = new ArrayList<>();

        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);

        Document doc = Jsoup.parse(xml);

        Elements es = doc.select("Context");

        es.forEach(e -> {
            boolean reloadable = Convert.toBool(e.attr("reloadable"), true);
            Context context = new Context(e.attr("path"), e.attr("docBase"), host, reloadable);
            result.add(context);
        });
        return result;
    }

    public static String getServiceName() {
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document doc = Jsoup.parse(xml);
        Element host = doc.select("service").first();
        return host.attr("name");
    }

    public static List<Host> getHosts(Engine engine) {
        List<Host> result = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Elements es = d.select("Host");
        es.forEach(e -> {
            Host host = new Host(e.attr("name"), engine);
            result.add(host);
        });
        return result;
    }

    public static String getEngineDefaultHost() {
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Element host = d.select("Engine").first();
        return host.attr("defaultHost");
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

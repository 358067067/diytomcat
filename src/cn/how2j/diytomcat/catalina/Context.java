package cn.how2j.diytomcat.catalina;

import cn.how2j.diytomcat.exception.WebConfigDuplicatedException;
import cn.how2j.diytomcat.util.ContextXMLUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.*;

public class Context {
    private String path;
    private String docBase;

    private File contextWebXmlFile;
    //地址对应类名
    private Map<String, String> url_servletClassName;
    //地址对应名称
    private Map<String, String> url_servletName;
    private Map<String, String> servletName_className;
    private Map<String, String> className_servletName;

    private void deploy() {
        TimeInterval timeInterval = DateUtil.timer();
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        init();
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",this.getDocBase(),timeInterval.intervalMs());
    }

    private void init() {
        if (!contextWebXmlFile.exists())
            return;

        try {
            checkDuplicated();
        } catch (WebConfigDuplicatedException e) {
            e.printStackTrace();
            return;
        }

        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);
        parseServletMapping(d);
    }

    private void parseServletMapping(Document doc) {
        // url_ServletName
        Elements mappingurlElements = doc.select("servlet-mapping url-pattern");
        mappingurlElements.forEach(e -> {
            String urlPattern = e.text();
            String servletName = e.parent().select("servlet-name").first().text();
            url_servletName.put(urlPattern, servletName);
        });
        // servletName_className / className_servletName
        Elements servletNameElements = doc.select("servlet servlet-name");
        servletNameElements.forEach(e -> {
            String servletName = e.text();
            String servletClass = e.parent().select("servlet-class").first().text();
            servletName_className.put(servletName, servletClass);
            className_servletName.put(servletClass, servletName);
        });
        // url_servletClassName
        Set<String> urls = url_servletName.keySet();
        urls.forEach(url -> {
            String servletName = url_servletName.get(url);
            String servletClassName = servletName_className.get(servletName);
            url_servletClassName.put(url, servletClassName);
        });
    }

    private void checkDuplicated(Document d, String mapping, String desc) throws WebConfigDuplicatedException {
        Elements elements = d.select(mapping);
        // 判断逻辑是放入一个集合，然后把集合排序之后看两临两个元素是否相同
        List<String> contents = new ArrayList<>();
        elements.forEach(e -> {
            contents.add(e.text());
        });

        Collections.sort(contents);

        for (int i = 0; i < contents.size() - 1; i++) {
            String contentPre = contents.get(i);
            String contentNext = contents.get(i + 1);
            if (contentPre.equals(contentNext)) {
                throw new WebConfigDuplicatedException(StrUtil.format(desc, contentPre));
            }
        }
    }

    private void checkDuplicated() throws WebConfigDuplicatedException {
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);

        checkDuplicated(d, "servlet-mapping url-pattern", "servlet url 重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-name", "servlet 名称重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-class", "servlet 类名重复,请保持其唯一性:{} ");
    }

    public Context(String path, String docBase) {
        this.path = path;
        this.docBase = docBase;
        this.contextWebXmlFile = new File(docBase, ContextXMLUtil.getWatchedResource());
        this.url_servletClassName = new HashMap<>();
        this.url_servletName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();

        deploy();
    }

    public String getServletClassName(String uri) {
        return url_servletClassName.get(uri);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDocBase() {
        return docBase;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }
}

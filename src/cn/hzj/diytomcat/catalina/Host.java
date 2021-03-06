package cn.hzj.diytomcat.catalina;

import cn.hzj.diytomcat.util.Constant;
import cn.hutool.log.LogFactory;
import cn.hzj.diytomcat.util.ServerXMLUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Host {

    private String name;
    private Map<String, cn.hzj.diytomcat.catalina.Context> contextMap;
    private cn.hzj.diytomcat.catalina.Engine engine;

    public Host(String name, Engine engine) {
        this.contextMap = new HashMap<>();
        this.name = name;
        this.engine = engine;

        scanContextsOnWebAppsFolder();
        scanContextsInServerXML();

    }

    private void scanContextsInServerXML() {
        List<cn.hzj.diytomcat.catalina.Context> contexts = ServerXMLUtil.getContexts(this);
        contexts.forEach(context -> {
            contextMap.put(context.getPath(), context);
        });
    }

    private void scanContextsOnWebAppsFolder() {
        File[] folders = Constant.webappsFolder.listFiles();
        Arrays.asList(folders).forEach(folder -> {
            if (folder.isDirectory())
                loadContext(folder);
        });
    }

    private void loadContext(File folder) {
        String path = folder.getName();
        if ("ROOT".equals(path))
            path = "/";
        else
            path = "/" + path;

        String docBase = folder.getAbsolutePath();
        cn.hzj.diytomcat.catalina.Context context = new cn.hzj.diytomcat.catalina.Context(path, docBase, this, true);

        contextMap.put(context.getPath(), context);
    }

    public cn.hzj.diytomcat.catalina.Context getContext(String path) {
        return contextMap.get(path);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void reload(cn.hzj.diytomcat.catalina.Context context) {
        LogFactory.get().info("Reloading Context with name [{}] has started", context.getPath());
        String path = context.getPath();
        String docBase = context.getDocBase();
        boolean reloadable = context.isReloadable();
        // stop
        context.stop();
        // remove
        contextMap.remove(path);
        // allocate new context
        cn.hzj.diytomcat.catalina.Context newContext = new Context(path, docBase, this, reloadable);
        // assign it to map
        contextMap.put(newContext.getPath(), newContext);
        LogFactory.get().info("Reloading Context with name [{}] has completed", context.getPath());
    }
}

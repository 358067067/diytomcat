package cn.hzj.diytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;
import cn.hzj.diytomcat.util.ServerXMLUtil;

import java.util.List;

public class Service {
    private String name;
    private cn.hzj.diytomcat.catalina.Engine engine;
    private cn.hzj.diytomcat.catalina.Server server;

    private List<Connector> connectors;
    public Service(cn.hzj.diytomcat.catalina.Server server){
        this.server = server;
        this.name = cn.hzj.diytomcat.util.ServerXMLUtil.getServiceName();
        this.engine = new cn.hzj.diytomcat.catalina.Engine(this);
        this.connectors = ServerXMLUtil.getConnectors(this);
    }

    public Engine getEngine() {
        return engine;
    }

    public Server getServer() {
        return server;
    }

    public void start() {
        init();
    }

    private void init() {
        TimeInterval timeInterval = DateUtil.timer();
        connectors.forEach(Connector::init);
        LogFactory.get().info("Initialization processed in {} ms",timeInterval.intervalMs());
        connectors.forEach(Connector::start);
    }

}

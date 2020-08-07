package cn.how2j.diytomcat;

import cn.how2j.diytomcat.catalina.*;

public class Bootstrap {

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

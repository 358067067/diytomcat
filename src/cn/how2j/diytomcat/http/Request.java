package cn.how2j.diytomcat.http;

import cn.how2j.diytomcat.catalina.Context;
import cn.how2j.diytomcat.catalina.Engine;
import cn.how2j.diytomcat.catalina.Service;
import cn.how2j.diytomcat.util.MiniBrowser;
import cn.hutool.core.util.StrUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Request extends BaseRequest {

    private Context context;

    public Context getContext() {
        return context;
    }

    private String requestString;
    private String uri;
    private Socket socket;
    private Service service;
    public Request(Socket socket, Service service) throws IOException {
        this.service = service;
        this.socket = socket;
        parseHttpRequest();
        if(StrUtil.isEmpty(requestString))
            return;
        parseUri();
        parseContext();
        if(!"/".equals(context.getPath())){
            uri = StrUtil.removePrefix(uri, context.getPath());
            if(StrUtil.isEmpty(uri))
                uri = "/";
        }
    }

    private void parseContext() {
        Engine engine = service.getEngine();
        context = engine.getDefaultHost().getContext(uri);
        if(null != context)
            return;
        String path = StrUtil.subBetween(uri, "/", "/");
        if (null == path)
            path = "/";
        else
            path = "/" + path;
        context = service.getEngine().getDefaultHost().getContext(path);
        if (null == context)
            context = service.getEngine().getDefaultHost().getContext("/");
    }

    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is, false);
        requestString = new String(bytes, "utf-8");
    }

    private void parseUri() {
        String temp;

        temp = StrUtil.subBetween(requestString, " ", " ");
        if (!StrUtil.contains(temp, '?')) {
            uri = temp;
            return;
        }
        temp = StrUtil.subBefore(temp, '?', false);
        uri = temp;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestString(){
        return requestString;
    }
}

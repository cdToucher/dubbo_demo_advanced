package me.myProjects.dubbo.demo.extension;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.http.HttpBinder;
import com.alibaba.dubbo.remoting.http.HttpHandler;
import com.alibaba.dubbo.remoting.http.HttpServer;

/**
 * Created by chendong on 2017/12/22.
 */
public class JettyHttpBinder implements HttpBinder {

    public JettyHttpBinder() {
    }

    public HttpServer bind(URL url, HttpHandler handler) {
        return new JettyHttpServer(url, handler);
    }
}

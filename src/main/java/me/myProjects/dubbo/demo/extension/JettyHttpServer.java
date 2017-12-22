package me.myProjects.dubbo.demo.extension;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.remoting.http.HttpHandler;
import com.alibaba.dubbo.remoting.http.servlet.DispatcherServlet;
import com.alibaba.dubbo.remoting.http.servlet.ServletManager;
import com.alibaba.dubbo.remoting.http.support.AbstractHttpServer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.log.Log;
import org.mortbay.log.StdErrLog;
import org.mortbay.thread.QueuedThreadPool;

/**
 * Created by chendong on 2017/12/22.
 */
public class JettyHttpServer extends AbstractHttpServer {


    private static final Logger logger = LoggerFactory.getLogger(com.alibaba.dubbo.remoting.http.jetty.JettyHttpServer.class);
    private Server server;
    private URL url;

    public JettyHttpServer(URL url, HttpHandler handler) {
        super(url, handler);
        this.url = url;
        Log.setLog(new StdErrLog());
        Log.getLog().setDebugEnabled(false);
        DispatcherServlet.addHttpHandler(url.getPort(), handler);
        int threads = url.getParameter("threads", 200);
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setDaemon(true);
        threadPool.setMaxThreads(threads);
        threadPool.setMinThreads(threads);
        SelectChannelConnector connector = new SelectChannelConnector();
        if (!url.isAnyHost() && NetUtils.isValidLocalHost(url.getHost())) {
            connector.setHost(url.getHost());
        }

        connector.setPort(8000);
        this.server = new Server();
        this.server.setThreadPool(threadPool);
        this.server.addConnector(connector);
        ServletHandler servletHandler = new ServletHandler();
        ServletHolder servletHolder = servletHandler.addServletWithMapping(DispatcherServlet.class, "/*");
        servletHolder.setInitOrder(2);
        Context context = new Context(this.server, "/", 1);
        context.setServletHandler(servletHandler);
        ServletManager.getInstance().addServletContext(url.getPort(), context.getServletContext());

        try {
            this.server.start();
        } catch (Exception var10) {
            throw new IllegalStateException("Failed to start jetty server on " + url.getAddress() + ", cause: " + var10.getMessage(), var10);
        }
    }

    public void close() {
        super.close();
        ServletManager.getInstance().removeServletContext(this.url.getPort());
        if (this.server != null) {
            try {
                this.server.stop();
            } catch (Exception var2) {
                logger.warn(var2.getMessage(), var2);
            }
        }

    }
}

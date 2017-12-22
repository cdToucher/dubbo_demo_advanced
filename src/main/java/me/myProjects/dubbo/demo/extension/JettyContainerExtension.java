package me.myProjects.dubbo.demo.extension;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.container.Container;
import com.alibaba.dubbo.container.page.PageServlet;
import com.alibaba.dubbo.container.page.ResourceFilter;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chendong on 2017/12/22.
 */
public class JettyContainerExtension implements Container {

    private static final Logger logger = LoggerFactory.getLogger(JettyContainerExtension.class);
    public static final String JETTY_PORT = "dubbo.jetty.port";
    public static final String JETTY_DIRECTORY = "dubbo.jetty.directory";
    public static final String JETTY_PAGES = "dubbo.jetty.page";
    public static final int DEFAULT_JETTY_PORT = 8080;
    public SelectChannelConnector connector;

    public JettyContainerExtension() {
    }

    public void start() {
        String serverPort = ConfigUtils.getProperty("dubbo.jetty.port");
        int port;
        if (serverPort != null && serverPort.length() != 0) {
            port = Integer.parseInt(serverPort);
        } else {
            port = 8080;
        }

        this.connector = new SelectChannelConnector();
        this.connector.setPort(8000);
        ServletHandler handler = new ServletHandler();
        String resources = ConfigUtils.getProperty("dubbo.jetty.directory");
        if (resources != null && resources.length() > 0) {
            FilterHolder resourceHolder = handler.addFilterWithMapping(ResourceFilter.class, "/*", 0);
            resourceHolder.setInitParameter("resources", resources);
        }

        ServletHolder pageHolder = handler.addServletWithMapping(PageServlet.class, "/*");
        pageHolder.setInitParameter("pages", ConfigUtils.getProperty("dubbo.jetty.page"));
        pageHolder.setInitOrder(2);
        Server server = new Server();
        server.addConnector(this.connector);
        server.addHandler(handler);

        try {
            server.start();
        } catch (Exception var8) {
            throw new IllegalStateException("Failed to start jetty server on " + NetUtils.getLocalHost() + ":" + port + ", cause: " + var8.getMessage(), var8);
        }
    }

    public void stop() {
        try {
            if (this.connector != null) {
                this.connector.close();
                this.connector = null;
            }
        } catch (Throwable var2) {
            logger.error(var2.getMessage(), var2);
        }

    }

}

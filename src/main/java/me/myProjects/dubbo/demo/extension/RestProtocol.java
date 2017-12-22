package me.myProjects.dubbo.demo.extension;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.http.HttpBinder;
import com.alibaba.dubbo.remoting.http.servlet.BootstrapListener;
import com.alibaba.dubbo.remoting.http.servlet.ServletManager;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.ServiceClassHolder;
import com.alibaba.dubbo.rpc.protocol.AbstractProxyProtocol;
import com.alibaba.dubbo.rpc.protocol.rest.RestServer;
import com.alibaba.dubbo.rpc.protocol.rest.RestServerFactory;
import com.alibaba.dubbo.rpc.protocol.rest.RpcContextFilter;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.util.GetRestful;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RestProtocol extends AbstractProxyProtocol {
    private static final int DEFAULT_PORT = 80;
    private final Map<String, RestServer> servers = new ConcurrentHashMap();
    private final RestServerFactory serverFactory = new RestServerFactory();
    private final List<ResteasyClient> clients = Collections.synchronizedList(new LinkedList());
    private volatile RestProtocol.ConnectionMonitor connectionMonitor;

    public RestProtocol() {
        super(new Class[]{WebApplicationException.class, ProcessingException.class});
    }

    public void setHttpBinder(HttpBinder httpBinder) {
        this.serverFactory.setHttpBinder(httpBinder);
    }

    public int getDefaultPort() {
        return 80;
    }

    protected <T> Runnable doExport(T impl, Class<T> type, URL url) throws RpcException {
        String addr = url.getIp() + ":" + url.getPort();
        Class implClass = ServiceClassHolder.getInstance().popServiceClass();
        RestServer server = this.servers.get(addr);
        if (server == null) {
            server = this.serverFactory.createServer(url.getParameter("server", "jetty"));
            server.start(url);
            this.servers.put(addr, server);
        }

        String contextPath = this.getContextPath(url);
        if ("servlet".equalsIgnoreCase(url.getParameter("server", "jetty"))) {
            ServletContext servletContext = ServletManager.getInstance().getServletContext(-1234);
            if (servletContext == null) {
                throw new RpcException("No servlet context found. Since you are using server='servlet', make sure that you've configured " + BootstrapListener.class.getName() + " in web.xml");
            }

            String webappPath = servletContext.getContextPath();
            if (StringUtils.isNotEmpty(webappPath)) {
                webappPath = webappPath.substring(1);
                if (!contextPath.startsWith(webappPath)) {
                    throw new RpcException("Since you are using server='servlet', make sure that the 'contextpath' property starts with the path of external webapp");
                }

                contextPath = contextPath.substring(webappPath.length());
                if (contextPath.startsWith("/")) {
                    contextPath = contextPath.substring(1);
                }
            }
        }

        final Class resourceDef = GetRestful.getRootResourceClass(implClass) != null ? implClass : type;
        server.deploy(resourceDef, impl, contextPath);
        RestServer finalServer = server;
        return () -> finalServer.undeploy(resourceDef);
    }

    protected <T> T doRefer(Class<T> serviceType, URL url) throws RpcException {
        if (this.connectionMonitor == null) {
            this.connectionMonitor = new RestProtocol.ConnectionMonitor();
        }

        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        connectionManager.setMaxTotal(url.getParameter("connections", 20));
        connectionManager.setDefaultMaxPerRoute(url.getParameter("connections", 20));
        this.connectionMonitor.addConnectionManager(connectionManager);
        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager);
        httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));

                String param;
                String value;
                do {
                    if (!it.hasNext()) {
                        return 30000L;
                    }

                    HeaderElement he = it.nextElement();
                    param = he.getName();
                    value = he.getValue();
                } while (value == null || !param.equalsIgnoreCase("timeout"));

                return Long.parseLong(value) * 1000L;
            }
        });
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, url.getParameter("timeout", 1000));
        HttpConnectionParams.setSoTimeout(params, url.getParameter("timeout", 1000));
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSoKeepalive(params, true);
        ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
        ResteasyClient client = (new ResteasyClientBuilder()).httpEngine(engine).build();
        this.clients.add(client);
        client.register(RpcContextFilter.class);
        String[] var8 = Constants.COMMA_SPLIT_PATTERN.split(url.getParameter("extension", ""));
        int var9 = var8.length;

        for (int var10 = 0; var10 < var9; ++var10) {
            String clazz = var8[var10];
            if (!StringUtils.isEmpty(clazz)) {
                try {
                    client.register(Thread.currentThread().getContextClassLoader().loadClass(clazz.trim()));
                } catch (ClassNotFoundException var13) {
                    throw new RpcException("Error loading JAX-RS extension class: " + clazz.trim(), var13);
                }
            }
        }

        ResteasyWebTarget target = client.target("http://" + url.getHost() + ":" + url.getPort() + "/" + this.getContextPath(url));
        return target.proxy(serviceType);
    }

    protected int getErrorCode(Throwable e) {
        return super.getErrorCode(e);
    }

    public void destroy() {
        super.destroy();
        if (this.connectionMonitor != null) {
            this.connectionMonitor.shutdown();
        }

        Iterator var1 = this.servers.entrySet().iterator();

        while (var1.hasNext()) {
            Map.Entry entry = (Map.Entry) var1.next();

            try {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Closing the rest server at " + (String) entry.getKey());
                }

                ((RestServer) entry.getValue()).stop();
            } catch (Throwable var5) {
                this.logger.warn("Error closing rest server", var5);
            }
        }

        this.servers.clear();
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Closing rest clients");
        }

        var1 = this.clients.iterator();

        while (var1.hasNext()) {
            ResteasyClient client = (ResteasyClient) var1.next();

            try {
                client.close();
            } catch (Throwable var4) {
                this.logger.warn("Error closing rest client", var4);
            }
        }

        this.clients.clear();
    }

    protected String getContextPath(URL url) {
        int pos = url.getPath().lastIndexOf("/");
        return pos > 0 ? url.getPath().substring(0, pos) : "";
    }

    protected class ConnectionMonitor extends Thread {
        private volatile boolean shutdown;
        private final List<ClientConnectionManager> connectionManagers = Collections.synchronizedList(new LinkedList());

        protected ConnectionMonitor() {
        }

        public void addConnectionManager(ClientConnectionManager connectionManager) {
            this.connectionManagers.add(connectionManager);
        }

        public void run() {
            label31:
            while (true) {
                try {
                    if (!this.shutdown) {
                        synchronized (this) {
                            this.wait(1000L);
                            Iterator var2 = this.connectionManagers.iterator();

                            while (true) {
                                if (!var2.hasNext()) {
                                    continue label31;
                                }

                                ClientConnectionManager connectionManager = (ClientConnectionManager) var2.next();
                                connectionManager.closeExpiredConnections();
                                connectionManager.closeIdleConnections(30L, TimeUnit.SECONDS);
                            }
                        }
                    }
                } catch (InterruptedException var6) {
                    this.shutdown();
                }

                return;
            }
        }

        public void shutdown() {
            this.shutdown = true;
            this.connectionManagers.clear();
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
}

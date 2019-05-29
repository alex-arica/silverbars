package silverbars.util.http.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import silverbars.configuration.context.HttpServerConfig;

import javax.servlet.http.HttpServlet;
import java.util.concurrent.BlockingQueue;

public class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private final ServletContextHandler servletContextHandler;
    private final int port;
    private Server jettyHttpServer;

    public HttpServer(final HttpServerConfig httpServerConfig) {
        this.servletContextHandler = new ServletContextHandler();
        this.port = httpServerConfig.getPort();
    }

    public void startServer() {
        try {
            final int minThreads = 8;
            final int maxThreads = 100;
            final int maxElementsInThreadPool = 1000;
            final int threadIdleTimeout = 10000;
            final BlockingQueue<Runnable> boundedBlockingQueue = new BlockingArrayQueue<>(minThreads, minThreads, maxElementsInThreadPool);
            final QueuedThreadPool queuedThreadPool = new QueuedThreadPool(maxThreads, minThreads, threadIdleTimeout, boundedBlockingQueue);
            jettyHttpServer = new Server(queuedThreadPool);

            final ServerConnector serverConnector = new ServerConnector(jettyHttpServer);
            serverConnector.setPort(port);
            jettyHttpServer.addConnector(serverConnector);
            jettyHttpServer.setHandler(servletContextHandler);
            jettyHttpServer.start();

            LOGGER.info("Started HTTP server");

        } catch (Exception ex) {
            final String errorMsg = "Error while starting HTTP server";
            LOGGER.error(errorMsg);
            throw new RuntimeException(errorMsg, ex);
        }
    }

    public boolean isStarted() {
        return jettyHttpServer != null && jettyHttpServer.isStarted();
    }

    public void stopServer() throws Exception {
        jettyHttpServer.stop();
        LOGGER.info("Stopped HTTP server");
    }

    public void addHttpServlet(final HttpServlet httpServlet,
                               final String restPath){
        servletContextHandler.addServlet(new ServletHolder(httpServlet), restPath);
    }
}

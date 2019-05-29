package silverbars;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import silverbars.configuration.context.BeansContext;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static BeansContext beansContext;

    public static void main(String[] args) {
        createContextBeans();
        addShutdownHook();
        configureRestEndpoints();

        LOGGER.info("Service is ready");
    }

    private static void createContextBeans() {
        beansContext = new BeansContext();
        beansContext.createBeans();
    }

    public static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    beansContext.httpServer.stopServer();
                } catch (Exception ex) {}
            }
        });
    }

    private static void  configureRestEndpoints() {
        beansContext.httpServer.startServer();
        beansContext.httpServer.addHttpServlet(beansContext.getHealthRestAdapter, "/health");
        beansContext.httpServer.addHttpServlet(beansContext.getOrdersSummaryRestAdapter, "/orders/summary");
        beansContext.httpServer.addHttpServlet(beansContext.orderOperationsRestAdapter, "/order");
    }
}

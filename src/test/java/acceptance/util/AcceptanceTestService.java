package acceptance.util;

import silverbars.Main;

public class AcceptanceTestService {

    public static void start() {
        new Main().main(null);
        while (!Main.beansContext.httpServer.isStarted()) {}
    }

    public static void stop() {

        try {
            Main.beansContext.httpServer.stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (Main.beansContext.httpServer.isStarted()) {}
    }

}

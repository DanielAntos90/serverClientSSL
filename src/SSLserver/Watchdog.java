package SSLserver;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Watchdog extends Thread {
    private LinkedList<SocketHandler> handlerList = new LinkedList<>();
    private static Logger logger = Logger.getLogger(Watchdog.class.getName());

    public void run() {
        while (true) {
            try {
                for (SocketHandler handler : handlerList) {
                    if (handler.isInactive()) {
                        logger.log(Level.INFO, "Killing client");
                        handler.terminate();
                        handlerList.remove(handler);
                    }
                }
                Thread.sleep(10000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void register(SocketHandler handler) {
        handlerList.add(handler);
    }
}
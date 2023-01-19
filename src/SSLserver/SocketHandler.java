package SSLserver;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

class SocketHandler implements Runnable {
    protected SSLSocket socket;
    private static Logger logger = Logger.getLogger(Watchdog.class.getName());
    protected long lastActive = System.currentTimeMillis();;

    public SocketHandler(SSLSocket socket) {
        this.socket = socket;
        printSocketInfo();
    }

    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            int letter;
            while ((letter = in.read()) != -1) {
                lastActive = System.currentTimeMillis();

                out.write(letter);
                out.flush();
            }

            logger.log(Level.INFO, String.format("Client disconnect: %s:%s",socket.getInetAddress(),socket.getPort()));
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void terminate() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.INFO, "Unable to close socket");
        }
    }

    public boolean isInactive() {
        if((System.currentTimeMillis() - lastActive) > 10000) {
            return true;
        }
        return false;
    }

    private void printSocketInfo() {
        System.out.println("Socket class: "+this.socket.getClass());
        System.out.println("   Remote address = "+this.socket.getInetAddress().toString());
        System.out.println("   Remote port = "+this.socket.getPort());
        System.out.println("   Local socket address = " +this.socket.getLocalSocketAddress().toString());
        System.out.println("   Local address = "+this.socket.getLocalAddress().toString());
        System.out.println("   Local port = "+this.socket.getLocalPort());
        System.out.println("   Need client authentication = "+this.socket.getNeedClientAuth());

        SSLSession ss = this.socket.getSession();
        System.out.println("   Cipher suite = "+ss.getCipherSuite());
        System.out.println("   Protocol = "+ss.getProtocol());
    }
}
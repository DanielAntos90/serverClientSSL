package SSLserver;
import java.io.*;
import java.security.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.*;
public class Server {
   private String ksName = "";
   private char ksPass[];
   private char ctPass[];
   private SSLServerSocket serverSocket = null;

   public Server(int port, String ksName, char ksPass[], char ctPass[]) {
      this.ksName = ksName;
      this.ksPass = ksPass;
      this.ctPass = ctPass;
      startServer();
      runService();
   }
   private void startServer() {
      try {
         KeyStore ks = KeyStore.getInstance("pkcs12");
         ks.load(new FileInputStream(ksName), ksPass);
         KeyManagerFactory kmf =KeyManagerFactory.getInstance("SunX509");
         kmf.init(ks, ctPass);
         SSLContext sc = SSLContext.getInstance("TLS");
         sc.init(kmf.getKeyManagers(), null, null);

         SSLServerSocketFactory ssf = sc.getServerSocketFactory();
         serverSocket = (SSLServerSocket) ssf.createServerSocket(8888);
         printServerSocketInfo();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   private void runService() {
      try {
         ExecutorService exec = Executors.newCachedThreadPool();
         var watchdog = new Watchdog();
         watchdog.start();

         while (true) {
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            SocketHandler handler = new SocketHandler(socket);
            watchdog.register(handler);
            exec.execute(handler);
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            serverSocket.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

   }
   private void printServerSocketInfo() {
      System.out.println("Server socket class: "+serverSocket.getClass());
      System.out.println("   Socket address = "+serverSocket.getInetAddress().toString());
      System.out.println("   Socket port = " +serverSocket.getLocalPort());
      System.out.println("   Need client authentication = " +serverSocket.getNeedClientAuth());
      System.out.println("   Want client authentication = "+serverSocket.getWantClientAuth());
      System.out.println("   Use client mode = " +serverSocket.getUseClientMode());
   } 
}
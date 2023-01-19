import SSLserver.Server;

public class Main {

    public static void main(String[] args) {

        try {
            Server s = new Server(8888,"VASEJMENO.p12","testpass".toCharArray(),"testpass".toCharArray());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}



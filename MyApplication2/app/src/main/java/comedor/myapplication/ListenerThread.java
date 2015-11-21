package comedor.myapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by johnjmar on 11/20/15.
 */
class ListenerThread implements Runnable {

    private int MY_PORT;
    private ServerSocket listenSckt;

    public ListenerThread (Integer port) {
        this.MY_PORT = port;
    }

    public void run() {
        Socket socket;

        try {
            this.listenSckt = new ServerSocket(MY_PORT);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                socket = listenSckt.accept();
                IncomingMSGThread msgThread = new IncomingMSGThread(socket);
                new Thread(msgThread).start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }//end while
    } // end run
}

class IncomingMSGThread implements Runnable {
    private Socket clientSckt;
    private BufferedReader in;

    public IncomingMSGThread(Socket clientSocket) {
        this.clientSckt = clientSocket;
        try {
            this.in =
                    new BufferedReader(new InputStreamReader(this.clientSckt.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            try {
                String INCOMING = in.readLine();

                //TODO -- ACT DEPENDING ON ID and MSG
                //SERVER_ID=5000 (same as server port??) --- OPS={CONFIRM,UPDATE,CLEAR,INFO}
                //CLIENT_IDS={0:size_of_CLK} -- OP=UPDATECLKS
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }//end while
    }//end run
}//end class


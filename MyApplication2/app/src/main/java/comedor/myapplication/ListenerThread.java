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
    private MainActivity originator;

    public ListenerThread (MainActivity source, Integer port) {
        this.originator = source;
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

    private static final String CONFIRM = "confirm";
    private static final String UPDATE = "update";
    private static final String CLEAR = "clear";
    private static final String INFO = "info";

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
                String[] fields = INCOMING.split("!!"); //main delimeter = !!

                //SERVER_ID=5000 (same as server port??) --- OPS={CONFIRM,UPDATE,CLEAR,INFO}
                //INCOMING = SID + CLK + MSG , MSG=<TYPE>#<BODY>
                String SID = "5000"; //TODO SET SERVER ID ACCORDINGLY
                if (fields[0].equals(SID)) {
                    if (fields.length == 3 ) {
                        updateCLK(Integer.parseInt(SID), fields[1]);
                        processMSG(fields[2]);
                    }
                }
                //CLIENT_IDS={0:size_of_CLK} -- OP=UPDATECLKS
                //INCOMING = ID + CLK
                else {
                    if (fields.length == 2) {
                        try {
                            Integer ID  =Integer.parseInt(fields[1]);
                            //TODO -- check ID is valid
                            updateCLK(ID, fields[1]);
                        }
                        catch (Exception e) { e.printStackTrace(); }
                    }
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }//end while
    }//end run

    public void updateCLK(Integer ID, String strCLK) {

        String[] vector = strCLK.replaceAll("\\[|\\]", "").split(",");
        Integer component;
        for (int i = 0; i < vector.length; i++) {
            try {
                component = Integer.parseInt(vector[i]);
                //TODO -- COMPARE AND SET rcvdCLK vs. localCLK components
            }
            catch (NumberFormatException nfe) {}
        }
    }

    public void processMSG(String fullMsg) { // MSG=<TYPE>#<BODY>
        String[] fields = fullMsg.split("#");

        //TODO -- ACT DEPENDING ON TYPE and MSG
        switch (fields[0]) { //TYPE
            case INFO:
                //do something with BODY (fields[1])
                break;
            case CLEAR:
                //do something with BODY (fields[1])
                break;
            case UPDATE:
                //do something with BODY (fields[1])
                break;
            case CONFIRM:
                //do something with BODY (fields[1])
                break;
            default:
                //throw ERROR or something
                break;
        }
    }
}//end class


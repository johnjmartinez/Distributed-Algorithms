package comedor.myapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by johnjmar on 11/20/15.
 *
 * Background client thread that will listen for any incoming message from peers or server
 *
 */
class ListenerThread implements Runnable {

    private int MY_PORT;
    private ServerSocket listenSckt;
    //NOT USED -- private MainActivity originator;

    public ListenerThread (Integer port) {
        //this.originator = source;
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

    //define all possible incoming messages from server
    //NOT USED -- private static final String CONFIRM = "confirm";
    private static final String UPDATE = "update";
    private static final String CLEAR = "clear";
    private static final String INFO = "info";
    private static final String NEW_PEER = "new table";

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
            Log.e("LISTENER", "IOException caught "+ e.toString());
        }
    }

    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            try {
                String INCOMING = in.readLine();
                String[] fields = INCOMING.split("!!"); //main delimeter = !!

                //SERVER_ID=6000 (same as server port??) --- OPS={<see below>}
                //INCOMING = SID + CLK + TAG + MSG
                String SID = "6000"; //SET SERVER ID ACCORDINGLY (SID==PORT)
                if (fields[0].equals(SID)) {
                    if (fields.length == 4 ) {
                        updateCLK(Integer.parseInt(SID), fields[1]);
                        processMSG(fields[2],fields[3]);
                        //NOPE! -- Send ACK back to server?
                    }
                }

                //CLIENT_IDS={0:size_of_CLK} -- OP=UPDATECLKS
                //INCOMING = ID + CLK
                else {
                    if (fields.length == 2) {
                        Integer ID = Integer.parseInt(fields[1]);

                        //Check ID is valid --- getting real ID, NOT id-1
                        if (ID > MainActivity.getCLK().length ||
                                MainActivity.IP_MAP[ID - 1] != clientSckt.getInetAddress().getHostAddress()) {
                            throw new IllegalStateException("Invalid IP in Listener Thread");
                        }
                        else {
                            updateCLK(ID, fields[1]);
                        }
                    }
                    else {
                        throw new IllegalStateException("Invalid MSG received " + INCOMING);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e("LISTENER", "Exception caught " + e.toString());
            }
        }//end while
    }//end run

    synchronized public void updateCLK(Integer ID, String strCLK) {

        Log.d("LISTENER", "CLK update from " + ID);

        Integer[] clk = MainActivity.getCLK();
        String[] vector = strCLK.replaceAll("\\[|\\]", "").split(","); //get rid of brackets, split
        Integer newComponent = null;

        if (clk == null) {
            clk = new Integer[vector.length]; // All zeroes
        }

        for (int i = 0; i < vector.length; i++) {
            try {
                newComponent = Integer.parseInt(vector[i]);

                //COMPARE AND SET rcvdCLK vs. localCLK newComponents
                clk[i] = (clk[i] > newComponent ? clk[i] : newComponent);

            }
            catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                Log.e("LISTENER", "Invalid new CLK vector component " + newComponent);

            }
            catch (Exception e) {
                e.printStackTrace();
                Log.e("LISTENER", "CLK update ERROR " + e.toString());
            }
        }

        MainActivity.updateCLK(clk);
        MainActivity.tickCLK();

        Log.d("LISTENER", "CLK update complete");

    }

    public void processMSG(String tag, String msg ) {

        String[] fields = null;

        //CHECKS on msg string
        if (msg == null || msg == "") { fields = new String[0]; }
        else if (msg.contains("#")) { fields = msg.split("#"); }

        //ACT DEPENDING ON TAG and MSG BODY WITH POPUPS
        switch (tag.toLowerCase()) { //TYPE
            case NEW_PEER: //SID!!CLK!!NEW TABLE!!IP_ARRAY
            case INFO:     //SID!!CLK!!INFO!!IP_ARRAY
                //No need to use fields since msg = IP_ARRAY
                MainActivity.IP_MAP = msg.replaceAll("\\[|\\]|\\s+", "").split(",");
                break;
            case CLEAR:
                MainActivity.LIVE_ORDER = false;
                MainActivity.foodQuantity = new HashMap<>(); //body == null
                break;
            case UPDATE:
                //always expecting ONE item ONLY
                if ( msg.contains("=-1") ) {
                    //delete 1 from item qty.
                    MainActivity.removeFromTicket(msg.split("=")[0]);
                }
                else if ( msg.contains("=1")) {
                    //add 1 to item qty.
                    MainActivity.addToTicektOrder(msg.split("=")[0]);
                }
                //TODO -- set toaster
                break;
            default:
                //throw ERROR or something
                break;
        }
    }
}//end class


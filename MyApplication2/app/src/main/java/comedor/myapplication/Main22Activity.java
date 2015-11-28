package comedor.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main22Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) { //CONFIRM SCREEN
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);

    }


    public void sendTicket() {

        String ticket = serializeTicket(MainActivity.TICKET);
        Log.d("CONFIRM", "Sending ticket to server\n"+ticket);

        MainActivity.tickCLK();

        String tag = "ORDER!!";
        Integer[] clk = MainActivity.getCLK();

        broadcastPeers(clk);
        ServerReq.out(MainActivity.MY_ID, clk, tag + ticket);

        Log.d("CONFIRM", "DONE");

    }

    public String serializeTicket (HashMap<String, String> t) {

        String out="";
        for( Map.Entry<String, String> entry : t.entrySet()) {
            out += entry.getKey()+"="+entry.getValue()+"#";
        }
        return out;
    }


    public void broadcastPeers(Integer[] clk) {

        Log.d("PEER_BRDCAST", "Starting broadcast of clk "+ clk);

        for (int i = 0; i < clk.length; i++) {
            if (i+1 == MainActivity.MY_ID) { continue; }
            new Thread(new PeerMsg( MainActivity.IP_MAP[i], clk )).start();
        }
        Log.d("PEER_BRDCAST", "Done");
    }
}

class PeerMsg implements Runnable {

    String remoteIP;
    Integer[] clk;

    public PeerMsg(String ip , Integer c[]){
        this.remoteIP = ip;
        this.clk = c;
    }

    public void run() {

        Socket sckt;
        String OUT = MainActivity.MY_ID.toString() + "!!" + Arrays.toString(clk);

        try {
            sckt = new Socket(remoteIP, MainActivity.CLIENT_PORT);
            PrintWriter outToPeer = new PrintWriter(sckt.getOutputStream(), true);

            outToPeer.println(OUT);
            sckt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("PEER_BRDCAST", "IOException caught " + e.toString() + ". Dest:" + remoteIP);
        }
    }
}

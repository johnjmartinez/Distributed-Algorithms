package comedor.myapplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by johnjmar on 11/20/15.
 *
 * Class that clients will use to send messages to the Server
 *
 */
class ServerReq {

    private static final Integer SERVER_PORT = 6000; //TEST -- TBD
    private static final String SERVER_IP = ""; //TEST -- TBD

    //CLIENT SENDS ID + CLK + MSG TO SERVER, EXPECTS ACK or OK
    //OUTPUT TO SERVER FOLLOWS THE FOLLOWING STRING FORMAT EXCEPT DURING INIT
    //"client_id!![v1, v2, v3, v4, ... , vn]!!ORDER!!food_item1=qty1#food_item2=qty2#...#food_itemn=qtyn"
    static String out (Integer ID, Integer[] CLK, String tagNmsg) {

        Socket clientSckt;
        String answer = "ERROR";
        String OUT = ID.toString() + "!!" + Arrays.toString(CLK) + "!!" + tagNmsg;

        try {
            clientSckt = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
            PrintWriter outToServer = new PrintWriter(clientSckt.getOutputStream(), true);
            BufferedReader inFromServer =
                    new BufferedReader(new InputStreamReader(clientSckt.getInputStream()));

            outToServer.println(OUT);
            while (!inFromServer.ready()) {
                Thread.yield();
            }

            //EXPECTED response from server:
            //OK/ACK or ERROR if attrb check fail. ALWAYS expecting CLK[] on response..
            answer = inFromServer.readLine();
            
            clientSckt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            //continue;
        }

        return answer; //raw answer returned
        //return "SID!![1, 0 , 2, 1, 2]!!ACK!![IP MAP]"; ///INIT TEST
    }
}
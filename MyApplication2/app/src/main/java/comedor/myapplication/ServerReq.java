package comedor.myapplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by johnjmar on 11/20/15.
 */
class ServerReq {

    private static final Integer SERVER_PORT = 5000; //TEST
    private static final String SERVER_IP = "128.0.0.1"; //TEST

    static String out (Integer ID, Integer[] CLK, String msg) {

        Socket clientSckt;
        String answer = "ERROR";
        String OUT = ID.toString() + "!!" + Arrays.toString(CLK) + "!!" + msg;

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
            //OK/ACK or ERROR if attrb check fail. ALWAYS expecting CLK[] on response.
            //No need for SID since peer initiated connection.
            answer = inFromServer.readLine();
            
            clientSckt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            //continue;
        }

       //return answer; //raw answer returned
        return "[1, 0 , 2, 1, 2]!!ACK"; ///TEST


    }
}
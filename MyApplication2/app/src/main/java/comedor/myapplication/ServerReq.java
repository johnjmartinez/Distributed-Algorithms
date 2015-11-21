package comedor.myapplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by johnjmar on 11/20/15.
 */
class ServerReq {

    private static final Integer SERVER_PORT = 5000; //TEST
    private static final String SERVER_IP = "128.0.0.1"; //TEST

    static String out (String msg) {

        Socket clientSckt;
        String answer = "ERROR";

        try {
            clientSckt = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
            PrintWriter outToServer = new PrintWriter(clientSckt.getOutputStream(), true);
            BufferedReader inFromServer =
                    new BufferedReader(new InputStreamReader(clientSckt.getInputStream()));

            outToServer.println(msg);
            while (!inFromServer.ready()) {;}

            //EXPECTED response from server:
            //OK/ACK or ERROR if attrb check fail. ALWAYS expecting CLK[] on response.
            answer = inFromServer.readLine();
            
            clientSckt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            //continue;
        }

       return answer+"\n";
    }
}
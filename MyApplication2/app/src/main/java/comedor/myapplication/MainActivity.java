package comedor.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.net.Inet4Address;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity { //LOGIN SCREEN

    public static final String EXTRA_MESSAGE = "com.ajaramillo.distributedorderingsystem.MESSAGE"; //?????
    public static final Integer CLIENT_PORT = 1234; //HARDCODED
    public static Integer MY_ID = null;   //SET ON LOGIN
    private static Integer[] CLK = null;  //RCVD FROM SERVER DURING INIT
    public static String[] IP_MAP = null; //RCVD FROM SERVER DURING INIT
    public static HashMap<String, String> TICKET; //MAIN HASH FOR CLIENT INTERACTION

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void display (View view) {
        EditText name =     (EditText) findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText4);
        TextView fail =     (TextView) findViewById(R.id.editText6);

        String table_num = name.getText().toString();
        String pwd = password.getText().toString();

        if (pwd.equals("R0sales")) {

            /**
             * ON SUCCESSFUL LOGIN, START BACKGROUND LISTENER THREAD AFTER INIT WITH SERVER
             */

            Log.d("INIT", "Starting initialization of table " + MY_ID);
            fail.setVisibility(View.INVISIBLE);
            MY_ID = Integer.parseInt(table_num); //numbered starting from 1

            Log.d("INIT", " Sending server request msg");
            String answer = ServerReq.out(MY_ID, CLK, "INIT!!"); //TODO -- SEND RealID or ID-1?

            /**
             * SERVER RESPONSE CHECKS FOLLOW
             * Expecting initResponse = CLK + TAG + IP_ARRAY, TAG={"OK", "ACK"}
             * i.e. "[1, 2, 0, 1, 0]!!ACK!![IP1, IP2, 0, IP4, 0]"
             */

            //Check if MSG contains ERROR (retry?) or ANS does not have enough fields
            if(answer.equals("") || answer.equals("ERROR") || answer == null) {
                Log.e("INIT", "Bad response from server");
                fail.setText("Bad response from server");
                fail.setVisibility(View.VISIBLE);
                Log.e("INIT", "Bad response from server");
                return;
            }

            //Check fields
            String[] fields = answer.split("!!");
            try {
                if ( fields.length != 3 || fields[0].equals("") || fields[2].equals("") ||
                        !(fields[1].equals("OK") || fields[1].equals("ACK")) ) {
                    Log.e("INIT", "Bad response from server "+ answer);
                    fail.setText("Bad response from server " + answer);
                    fail.setVisibility(View.VISIBLE);
                    return;
                }
            }
            catch (Exception e) { // 'if' might throw 'array out of bound' or other exception.
                Log.e("INIT", "Exception "+ e.toString() + ". Bad response from server "+ answer);
                fail.setText("Bad response from server: " + answer);
                fail.setVisibility(View.VISIBLE);
                return;
            }

            Log.d("INIT", "Assigning CLK to Client " + fields[0]);
            //String array de-serializer (inverse of Arrays.toString(CLK_ARR))
            //http://stackoverflow.com/a/7646415/4570161
            String[] clk_vector = fields[0].replaceAll("\\[|\\]", "").split(",");
            IP_MAP = fields[2].replaceAll("\\[|\\]|\\s+", "").split(",");

            //Check IP_MAP and clk_vector are of same size
            if( IP_MAP.length !=  clk_vector.length ) {
                fail.setText("Mismatch in IP map and clk sizes");
                Log.e("INIT", "Mismatch: ip map size =" + IP_MAP.length + ". clk size" +
                        clk_vector.length );
                fail.setVisibility(View.VISIBLE);
                return;
            }

            //Check id is within size of CLK[] ARR
            if( MY_ID > clk_vector.length ) {
                fail.setText("Invalid table number "+ MY_ID);
                fail.setVisibility(View.VISIBLE);
                return;
            }

            //Check ID matches IP MAP location
            try {
                String MY_IP = Inet4Address.getLocalHost().getHostAddress();
                if (IP_MAP[MY_ID-1] != MY_IP) {
                    fail.setText("Mismatch of CLIENT IP in IP MAP");
                    Log.e("INIT", "Mismatch: IP MAP OF MY_ID: " + IP_MAP[MY_ID-1] + ". MY_IP:" + MY_IP);
                    fail.setVisibility(View.VISIBLE);
                    return;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return;
            }

            CLK = new Integer[clk_vector.length];
            for (int i = 0; i < clk_vector.length; i++) {
                try {
                    CLK[i] = Integer.parseInt(clk_vector[i]);
                }
                catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                    Log.e("INIT", "Invalid CLK vector component " + clk_vector[i]);
                    fail.setText("BAD CLK!!!");
                    fail.setVisibility(View.VISIBLE);
                    return;
                }
            }

            /**
             * Start listening for any incoming MSGs from either server or peers
             */
            Log.d("INIT", "Starting client listening thread");
            new Thread(new ListenerThread(CLIENT_PORT)).start();
            TICKET = new HashMap<>();

            Log.d("INIT", "Switching view to menu");
            Intent intent = new Intent(this, Main2Activity.class);
            //intent.putExtra(EXTRA_MESSAGE, user); //TODO -- not sure if correct.
            startActivity(intent);

        }
        else {
            fail.setVisibility(View.VISIBLE);
        }
    }

    synchronized public static void updateCLK(Integer[] newCLK) {
        CLK = newCLK;
    }

    synchronized public static Integer[] getCLK() {
        return CLK;
    }

    synchronized public static void tickCLK() {
        CLK[MY_ID-1]++;
    }
}//end MainActivity

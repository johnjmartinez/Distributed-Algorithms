package comedor.myapplication;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity { //LOGIN SCREEN

    public static final Integer CLIENT_PORT = 1234; //HARDCODED
    public static Integer MY_ID = null;   //SET ON LOGIN
    private static Integer[] CLK = null;  //RCV FROM SERVER DURING INIT
    public static String[] IP_MAP = null; //RCV FROM SERVER DURING INIT
    public static final HashMap<String, Double> PRICES; //MAIN HASH FOR PRICES
    public static HashMap<String, Integer> foodQuantity = new HashMap<String, Integer>(); //TICKET
    public static Boolean LIVE_ORDER = false;
    public static String MY_IP = null;
    private static Boolean REFRESH_VIEW = false;

    static {
        PRICES = new HashMap<String, Double>();
        PRICES.put("NEW THE BLAZIN' TEXAN", 15.99);
        PRICES.put("NEW ALL-DAY BRUNCH BURGER", 10.99);
        PRICES.put("7OZ GRILLED ONION SIRLOIN WITH STOUT GRAVY", 17.99);
        PRICES.put("HOT SHOT WHISKEY CHICKEN", 14.99);
        PRICES.put("SHRIMP WONTON STIR-FRY", 14.99);
        PRICES.put("TRIPLE CHOCOLATE MELTDOWN", 7.99);
        PRICES.put("CHICKEN CEASAR SALAD", 9.99);
        PRICES.put("ORIENTAL CHICKEN SALAD", 9.99);
        PRICES.put("FRESH FRUIT CITRONADE", 6.99);
        PRICES.put("HOT FUDGE SUNDAE DESSERT SHOOTER", 7.99);
    }

    public static double getItemPrice(String item) {
        // Method for returning the price of an item
        return PRICES.get(item);
    }

    public static void addToTicektOrder(String item) {
        // Check if the table order is already there, we can just update it
        // Else we need to instantiate the mapping and the add the item
        if (foodQuantity.containsKey(item)) {
            int x = foodQuantity.get(item);
            foodQuantity.put(item, x + 1);
        } else {
            foodQuantity.put(item, 1);
        }
    }

    public static void removeFromTicket(String item) {
        int val = MainActivity.foodQuantity.get(item);
        // Check if the value of the item being deleted is 1
        if (val == 1) {
            // Remove the item from the table so that we do not display it again
            MainActivity.foodQuantity.remove(item);
        } else {
            // The item quantity was more than 1 so we can just substract
            MainActivity.foodQuantity.put(item, val - 1);
        }
    }

    public static Integer getQuantity(){
        int num = 0;
        for (Integer q: foodQuantity.values()){
            num += q;
        }
        return num;
    }

    synchronized public static void setRefresh() {
        REFRESH_VIEW = true;
    }

    synchronized public static void unsetRefresh() {
        REFRESH_VIEW = false;
    }

    synchronized public static Boolean getRefreshStatus() {
        return REFRESH_VIEW;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void display (View view) {

        EditText name =     (EditText) findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText4);
        TextView fail =     (TextView) findViewById(R.id.editText6);
        fail.setVisibility(View.INVISIBLE);

        String table_num = name.getText().toString();
        String pwd = password.getText().toString();

        if (pwd.equals("R0sales"))  {

        /**
        *  ON SUCCESSFUL LOGIN, START BACKGROUND LISTENER THREAD AFTER INIT WITH SERVER
        */
            Log.d("INIT", "Starting initialization of table " + table_num);
            MY_ID = Integer.parseInt(table_num); //numbered starting from 1 -- real id == table id

            Log.d("INIT", " Sending server request msg");
            String answer = ServerReq.out(MY_ID, CLK, "INIT!!");

            /**
             * SERVER RESPONSE CHECKS FOLLOW
             * Expecting initResponse = SID + CLK + TAG + IP_ARRAY, TAG={"OK", "ACK"}
             * i.e. "6000!![1, 2, 0, 1, 0]!!ACK!![IP1, IP2, 0, IP4, 0]"
            */

            //Check if MSG contains ERROR (retry?) or ANS does not have enough fields
            if(answer.equals("") || answer.contains("ERROR") || answer == null) {
                Log.e("INIT", "Bad response from server (1)" + answer);
                fail.setText("Bad response from server (1)");
                fail.setVisibility(View.VISIBLE);
                return;
            }

            //Check fields
            //fields[0] = SID --- no need to check
            //fields[1] = CLK ARRAY
            //fields[2] = OK/ACK TAG
            //fields[3] = IP MAP ARRAY
            String[] fields = answer.split("!!");
            try {
                if ( fields.length != 4 || fields[1].equals("") || fields[3].equals("") ||
                        !(fields[2].equals("OK") || fields[2].equals("ACK")) ) {
                    Log.e("INIT", "Bad response from server (2)"+ answer);
                    fail.setText("Bad response from server (2)" + answer);
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

            Log.d("INIT", "Assigning CLK to Client " + fields[1]);
            //String array de-serializer (inverse of Arrays.toString(CLK_ARR))
            //http://stackoverflow.com/a/7646415/4570161
            String[] clk_vector = fields[1].replaceAll("\\[|\\]", "").split(",");
            IP_MAP = fields[3].replaceAll("\\[|\\]|\\s+", "").split(",");

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
                WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                int ip = wifiInfo.getIpAddress();
                MY_IP = Formatter.formatIpAddress(ip);
                if (!IP_MAP[MY_ID-1].equals(MY_IP)) {
                    fail.setText("Mismatch of CLIENT IP in IP MAP");
                    Log.e("INIT", "Mismatch: IP MAP OF MY_ID:" + IP_MAP[MY_ID-1] + ": -  MY_IP:" +
                            MY_IP + ":");
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
                    CLK[i] = Integer.parseInt(clk_vector[i].replaceAll("\\s+",""));
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
            new Thread(new ListenerThread(CLIENT_PORT, getApplication())).start();
            foodQuantity = new HashMap<>();
            tickCLK();

            Log.d("INIT", "Switching view to menu");
            Intent intent = new Intent(this, Main2Activity.class);
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



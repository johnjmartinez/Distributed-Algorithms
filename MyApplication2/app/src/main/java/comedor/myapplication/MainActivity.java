package comedor.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity { //LOGIN SCREEN

    public static final String EXTRA_MESSAGE = "com.ajaramillo.distributedorderingsystem.MESSAGE"; //?????

    public static final Integer MY_PORT = 1234; //HARDCODED
    public static Integer MY_ID = null;

    private static Integer[] CLK = null;
    public static HashMap<String, String> TICKET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void display(View view) {
        TextView fail =     (TextView) findViewById(R.id.editText6);
        EditText name =     (EditText) findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText4);

        String table_num = name.getText().toString();
        String pwd = password.getText().toString();

        if (pwd.equals("R0sales")) {
            /**
             * ON SUCCESSFUL LOGIN, START BACKGROUND LISTENER THREAD AFTER INIT WITH SERVER
             */

            Log.d("INIT", "Starting initialization of table " + MY_ID);
            MY_ID = Integer.parseInt(table_num); //test

            Log.d("INIT", " Sending server request msg");
            String answer = ServerReq.out(MY_PORT, CLK, "INIT");

            //Expecting initResponse=CLK+MSG, MSG=OK or ACK
            // i.e. "[1, 0 , 2, 1, 2]!!ACK"
            //TODO -- Add check if MSG contains ERROR (retry?) or ANS does not have enough fields
            String[] fields = answer.split("!!");
            String MSG = fields[1];

            //String array de-serializer (inverse of Arrays.toString(CLK_ARR))
            //http://stackoverflow.com/a/7646415/4570161
            Log.d("INIT", "Assigning CLK to Client " + fields[0]);
            String[] vector = fields[0].replaceAll("\\[|\\]", "").split(",");

            //TODO -- Add check so that user id is within size of CLK[] ARR
            CLK = new Integer[vector.length];
            for (int i = 0; i < vector.length; i++) {
                try {
                    CLK[i] = Integer.parseInt(vector[i]);
                }
                catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                    Log.e("INIT", "Invalid CLK vector component " + vector[i]);
                }
            }

            //TODO -- check id has not been taken
            //Start listening for any incoming MSGs from either server or peers
            Log.d("INIT", "Starting client listening thread");
            new Thread(new ListenerThread(MY_PORT)).start();
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
        CLK[MY_ID]++;
    }





}//end MainActivity







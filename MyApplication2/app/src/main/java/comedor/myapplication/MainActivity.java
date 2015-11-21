package comedor.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity { //LOGIN SCREEN

    public final static String EXTRA_MESSAGE = "com.ajaramillo.distributedorderingsystem.MESSAGE";

    private static Integer MY_PORT;
    private static Integer[] CLK = null;
    private Thread LISTENER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         NOT SURE IF NEEDED
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         **/
    }

    public void display(View view) {
        TextView fail =     (TextView) findViewById(R.id.editText6);
        EditText name =     (EditText) findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText4);

        String user = name.getText().toString();
        String pwd = password.getText().toString();

        if (pwd.equals("rosales") && user.equals("999")) {
            
            //TODO -- Add check so that user is within size of CLK[] ARR
            //MY_PORT = Integer.parseInt(user); //same as ID
            MY_PORT = 1234; //test

            //Expecting initResponse=CLK+MSG, MSG=OK or ACK
            // i.e. "[1, 0 , 2, 1, 2]!!ACK"
            //TODO -- Add check if MSG contains ERROR (retry?) or ANS doesnot have enough fields
            String answer = ServerReq.out(MY_PORT, CLK, "INIT");
            String[] fields = answer.split("!!");
            String MSG = fields[1];

            //http://stackoverflow.com/a/7646415/4570161
            String[] vector = fields[0].replaceAll("\\[|\\]", "").split(",");
            CLK = new Integer[vector.length];
            for (int i = 0; i < vector.length; i++) {
                try {
                    CLK[i] = Integer.parseInt(vector[i]);
                }
                catch (NumberFormatException nfe) {}
            }

            //Start listening for any incoming MSGs from either server or peers
            //TODO -- gotta save thread pointer somehow to access incoming MSGs?
            new Thread(new ListenerThread(MY_PORT)).start();
            
            Intent intent = new Intent(this, Main2Activity.class);
            intent.putExtra(EXTRA_MESSAGE, user);
            startActivity(intent);

        }
        else {
            fail.setVisibility(View.VISIBLE);
        }
    }
}







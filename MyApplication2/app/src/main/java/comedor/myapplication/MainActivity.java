package comedor.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
//import android.view.Menu;
//import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static final Integer SERVER_PORT = 5000; //TEST
    private static final String SERVER_IP = "128.0.0.1"; //TEST

    private static Integer[] CLK;
    private static Integer MY_PORT;
    private Thread LISTENER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //NOT SURE IF NEEDED
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * NOT USING MENU ---
     *
     * public boolean onCreateOptionsMenu(Menu menu) {
     * // Inflate the menu; this adds items to the action bar if it is present.
     * getMenuInflater().inflate(R.menu.menu_main, menu);
     * return true;
     * }
     * public boolean onOptionsItemSelected(MenuItem item) {
     * // Handle action bar item clicks here. The action bar will
     * // automatically handle clicks on the Home/Up button, so long
     * // as you specify a parent activity in AndroidManifest.xml.
     * int id = item.getItemId();
     * <p/>
     * //noinspection SimplifiableIfStatement
     * if (id == R.id.action_settings) {
     * return true;
     * }
     * <p/>
     * return super.onOptionsItemSelected(item);
     * }
     **/

    public void display(View view) {
        EditText fail = (EditText) findViewById(R.id.editText6); //should be Text or popup
        EditText name  = (EditText) findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText4);
        //RadioButton boton = (RadioButton) findViewById(R.id.radioButton);

        String user = name.getText().toString();
        String pwd = password.getText().toString();

        if (pwd.equals("rosales") && user.equals("999")) {
            //boton.setVisibility(View.VISIBLE);



            
            //TODO -- Add check so that user is within size of CLK[] ARR
            MY_PORT = Integer.parseInt(user);
            new Thread(new ListenerThread(MY_PORT)).start(); //gotta save pointer some how?

        } else {
            fail.setVisibility(View.VISIBLE);
        }
    }

}






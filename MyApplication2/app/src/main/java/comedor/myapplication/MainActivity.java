package comedor.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.ajaramillo.distributedorderingsystem.MESSAGE";
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


    public void display(View view) {
        EditText fail = (EditText) findViewById(R.id.editText6); //should be Text or popup
        EditText name  = (EditText) findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText4);

        String user = name.getText().toString();
        String pwd = password.getText().toString();

        if (pwd.equals("rosales") && user.equals("999")) {
            Intent intent = new Intent(this, Main2Activity.class);
            intent.putExtra(EXTRA_MESSAGE, user);
            startActivity(intent);
            
            //TODO -- Add check so that user is within size of CLK[] ARR
            MY_PORT = Integer.parseInt(user);
            new Thread(new ListenerThread(MY_PORT)).start(); //gotta save pointer some how?

        } else {
            fail.setVisibility(View.VISIBLE);
        }
    }

}






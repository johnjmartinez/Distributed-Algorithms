package comedor.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.ajaramillo.distributedorderingsystem.MESSAGE";
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

    /** NOT USING MENU ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }**/

    public void display(View view) {
        EditText clave = (EditText) findViewById(R.id.editText6);
        EditText name = (EditText) findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText4);

        String user = name.getText().toString();
        String pwd = password.getText().toString();

        if (pwd.equals("rosales") && user.equals("999")) {
            Intent intent = new Intent(this, Main2Activity.class);
            intent.putExtra(EXTRA_MESSAGE, user);
            startActivity(intent);


        } else {
            clave.setVisibility(View.VISIBLE);
        }
    }
}

package comedor.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Main22Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) { //CONFIRM SCREEN
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);

        /**
         * NOT NEEDED
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

}

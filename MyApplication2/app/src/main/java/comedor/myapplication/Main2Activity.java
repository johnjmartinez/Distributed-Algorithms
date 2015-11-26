package comedor.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = " comedor.myapplication.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) { //MENU SCREEN
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Intent intent = getIntent(); //for?
        //Button btn = (Button)findViewById(R.id.button3);
    }

    public void checkOut(View view) { //ALWAYS REQUIRE VIEW
        Intent myIntent = new Intent(this, Main22Activity.class);
        myIntent.putExtra(EXTRA_MESSAGE, "herd Derp");
        startActivity(myIntent);
    }
}

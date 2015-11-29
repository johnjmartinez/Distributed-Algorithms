package comedor.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    int quantity = 0;
    public final static String EXTRA_MESSAGE = " comedor.myapplication.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) { //MENU SCREEN
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Intent intent = getIntent(); //for?
        //Button btn = (Button)findViewById(R.id.button3);
    }

    public void increment(View view) {
        quantity = quantity + 1;
        display(quantity);

    }

// method for displaying quantity
    private void display(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity);
        quantityTextView.setText("" + number);
    }


    public void checkOut(View view) { //ALWAYS REQUIRE VIEW
        Intent myIntent = new Intent(this, Main22Activity.class);
        myIntent.putExtra(EXTRA_MESSAGE, "herd Derp");
        startActivity(myIntent);
    }
}

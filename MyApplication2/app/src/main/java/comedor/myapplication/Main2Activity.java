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

        String item = "default";
        // Store the table name that is clicked as a string
        switch (view.getId()) {
            case R.id.imagen1:
                item = "NEW THE BLAZIN' TEXAN";
                break;
            case R.id.imagen2:
                item = "NEW ALL-DAY BRUNCH BURGER";
                break;
            case R.id.imagen3:
                item = "7OZ GRILLED ONION SIRLOIN WITH STOUT GRAVY";
                break;
            case R.id.imagen4:
                item = "HOT SHOT WHISKEY CHICKEN";
                break;
            case R.id.imagen5:
                item = "SHRIMP WONTON STIR-FRY";
                break;
            case R.id.imagen6:
                item = "TRIPLE CHOCOLATE MELTDOWN";
                break;
            case R.id.imagen7:
                item = "CHICKEN CEASAR SALAD";
                break;
            case R.id.imagen8:
                item = "ORIENTAL CHICKEN SALAD";
                break;
            case R.id.imagen9:
                item = "FRESH FRUIT CITRONADE";
                break;
            case R.id.imagen10:
                item = "HOT FUDGE SUNDAE DESSERT SHOOTER";
                break;
        }
            MainActivity.addToTicektOrder(item);
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

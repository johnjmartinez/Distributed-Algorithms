package comedor.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    TextView quantityTextView;
    Refresher r = new Refresher();

    @Override
    protected void onCreate(Bundle savedInstanceState) { //MENU SCREEN
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        quantityTextView = (TextView) findViewById(R.id.quantity);
        display();
    }

    @Override
    public void onResume() {
        super.onResume();
        display();
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
            display();
    }

   //method for displaying quantity
    public void display() {
        quantityTextView.setText("" + MainActivity.getQuantity());

        Button out = (Button) findViewById(R.id.button3);
        if (MainActivity.LIVE_ORDER) { out.setText("UPDATE ORDER"); }
        else { out.setText("CHECK OUT"); }

        r.cancel(true);
        r = new Refresher();
        r.execute();
    }


    public void checkOut(View view) { //ALWAYS REQUIRE VIEW
        r.cancel(true);
        Intent myIntent = new Intent(this, Main22Activity.class);
        startActivity(myIntent);
    }


    private class Refresher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("REFRESHER", "Starting new thread");

            while (!Thread.currentThread().isInterrupted()  ) {
                try {
                    if (MainActivity.getRefreshStatus()) { break; }
                    if ( isCancelled() ) { return null; }
                    Thread.sleep(2000);
                    Log.v("REFRESHER", "...waiting...");
                }
                catch (InterruptedException ie) {
                    //Thread got cancelled.
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("REFRESHER", "triggered");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (MainActivity.getRefreshStatus()) {
                MainActivity.unsetRefresh();
                //DOESN'T DO SHITE --- quantityTextView.invalidate();
                Main2Activity.this.display();
                Log.d("REFRESHER", "done");
            }
            super.onPostExecute(result);
        }
    }
}

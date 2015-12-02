package comedor.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main22Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { //CONFIRM SCREEN
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);
        updateActivity();

    }

    public void updateActivity() {
        // Method for updating each individual views in this activity
        TextView order = (TextView) findViewById(R.id.order1);
        TextView qty = (TextView) findViewById(R.id.qty1);
        TextView foodCost = (TextView) findViewById(R.id.price_text_view);
        TextView taxCost = (TextView) findViewById(R.id.taxes_text_view);
        TextView cost = (TextView) findViewById(R.id.total_price_text_view);
        String formatItems;
        String formatQty;
        double price = 0;

        //Set buttons if order is live
        Button minus = (Button) findViewById(R.id.button_delete);
        Button out = (Button) findViewById(R.id.button4);
        if (MainActivity.LIVE_ORDER) {
            minus.setVisibility(View.INVISIBLE);
            out.setText("CONFIRM UPDATE");
        }
        else {
            minus.setVisibility(View.VISIBLE);
            out.setText("CONFIRM");
        }


        // Check if the table order does not exist or has no items
        if (MainActivity.foodQuantity.isEmpty()) {
            // Set empty strings and 0's
            formatItems = "";
            formatQty = "";
            price = 0;
        }

        else{
            // Iterate through the order HashMap and create a parsable string
            String formatOrder = formatItemsAndQty(MainActivity.foodQuantity);
            // Parse the string for the order items
            formatItems = formatOrder.split("###")[0];
            // Parse the string for the order item quantities
            formatQty = formatOrder.split("###")[1];
            // Calculate the price of the order based on the items and their quantity
            price = calculateTotal(formatItems, formatQty);
        }

        // Set the order item(s) in the activity
        order.setText(formatItems);
        // Set the order item quantity(s) in the activity
        qty.setText(formatQty);
        // Set the price to 2 decimal places
        double p = Math.round(price * 100) / 100.00;
        foodCost.setText("" + p);
        // Set teh comps to 2 decimal places
        double t = Math.round(.0825 * (price ) * 100) / 100.00;
        taxCost.setText("" + t);
        // Add up the numbers to get the total cost of the table order
        double tot = Math.round((price + t) * 100.00) / 100.00;
        cost.setText("$" + tot);
    }

    public String formatItemsAndQty(HashMap<String, Integer> mp){
        // Method for iterating through the order HashMap and format for parsing
        String items = "";
        String qty = "";
        // Create an iterator through the map
        Iterator it = mp.entrySet().iterator();
        // Loop until there are no items in the iterator
        while (it.hasNext()) {
            // Get the key-pair values
            Map.Entry pair = (Map.Entry)it.next();
            // Add the key(item) to the items string
            items += pair.getKey();
            // Add the pair(quantity) to the qty string
            qty += pair.getValue();
            // If there are more items add line break
            if (it.hasNext()) {
                items += "\n";
                qty += "\n";
            }
        }
        // Separate items and quantity by ### and return
        return items + "###" + qty;
    }

    public double calculateTotal(String items, String qty) {
        // Method for calculating the total price of the order
        double total = 0;
        double price = 0;
        int quantity = 0;
        // Split the items up into an array
        String[] itms = items.split("\n");
        // Split the quantities up into an array
        String[] qtys = qty.split("\n");
        // Loop through the arrays
        for (int i = 0; i < itms.length; i++) {
            // Get the price of the item
            price = MainActivity.getItemPrice(itms[i]);
            // Parse the quantity to an integer
            quantity = Integer.parseInt(qtys[i]);
            // Update the total with the product of the item price and item quantity
            total += price * quantity;
        }
        return total;
    }

    public String[] getItemList(){
        // Method for getting the list of item available items and prices
        HashMap<String, Double> prices = MainActivity.PRICES;
        // Create an iterator
        Iterator it = prices.entrySet().iterator();
        String items = "";
        // Iterate through the items adding them to a # separated string
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            items += pair.getKey() + "#";
        }
        return items.split("#");
    }

    public void addToOrder(View view){
        // Method for handling popup dialogue for adding an item to an order
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Main22Activity.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select Item to Add");
        // Use a single choice list for our dialogue
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                Main22Activity.this,
                android.R.layout.select_dialog_singlechoice);
        // Get the list of items we can add
        String[] items = getItemList();
        // Add each item to the adapter for the list
        for (int i = 0; i < items.length; i++) {
            arrayAdapter.add(items[i]);
        }
        // Create cancel button
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Set the adapter as the list
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the item that was chosen
                final String strName = arrayAdapter.getItem(which);
                // Create an second dialogue to confirm the selection
                AlertDialog.Builder builderInner = new AlertDialog.Builder(Main22Activity.this);
                builderInner.setTitle("Add " + strName + "?");
                // If the selection is confirmed, we need to handle special cases
                builderInner.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Check if the order was previously placed
                        if (MainActivity.foodQuantity.containsKey(strName)){
                            Log.d("Check", "Add to order 1");
                            // Check if the order already has this item in it
                            if (MainActivity.foodQuantity.containsKey(strName)){
                                Log.d("Check", "Add to order 2");
                                // If the item was already there, we need to update the quantity
                                int val = MainActivity.foodQuantity.get(strName);
                                Log.d("Check", "Add to order 3");
                                MainActivity.addToTicektOrder(strName);
                                Log.d("Check", "Add to order 4");
                            }
                            else {
                                Log.d("Check", "Add to order 5");
                                // If the item wasn't there, we just add a single item to the order
                                MainActivity.addToTicektOrder(strName);
                                Log.d("Check", "Add to order 6");
                            }
                        }
                        else {
                            Log.d("Check", "Add to order 7");
                            // If the order didn't exist, we need to create it
                            MainActivity.addToTicektOrder(strName);
                            Log.d("Check", "Add to order 8");
                        }
                        updateActivity();
                    }
                });
                // If the add is not confirmed, just exit
                builderInner.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    public void deleteFromOrder(View view) {
        // Method for handling the popup dialogue for deleting an order item
        // Do not create popup if the order is empty
        if (!MainActivity.foodQuantity.isEmpty()) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(Main22Activity.this);
            //builderSingle.setIcon(R.drawable.ic_launcher);
            builderSingle.setTitle("Select Item to Delete");
            // Use a single choice list for our dialogue
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    Main22Activity.this,
                    android.R.layout.select_dialog_singlechoice);
            // Get a string representation of the items and quantity of the order
            String itemsAndQty = formatItemsAndQty(MainActivity.foodQuantity);
            // Get a list of the items from the string
            String[] items = itemsAndQty.split("###")[0].split("\n");
            // Loop through the item list and add each to the adapter
            for (int i = 0; i < items.length; i++) {
                arrayAdapter.add(items[i]);
            }
            // If the delete is canceled, just exit
            builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            // If an item was selected, create a second popup to confirm
            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Get the item that was selected
                    final String strName = arrayAdapter.getItem(which);
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(Main22Activity.this);
                    builderInner.setTitle("Delete " + strName + "?");
                    // If the delete was confirmed, we need to handle special cases
                    builderInner.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            MainActivity.removeFromTicket(strName);

                            // Check to see if there are any items left in the order
                            if (MainActivity.foodQuantity.isEmpty()) {

                            }
                            updateActivity();
                        }
                    });
                    // If the delete was not confirmed, just exit
                    builderInner.setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderInner.show();
                }
            });
            builderSingle.show();
        }
    }


    public void sendTicket(View view) {

        String ticket = serializeTicket(MainActivity.foodQuantity);
        Log.d("CONFIRM", "Sending ticket to server\n" + ticket);

        MainActivity.tickCLK();

        String tag = "ORDER!!";
        Integer[] clk = MainActivity.getCLK();

        broadcastPeers(clk);
        ServerReq.out(MainActivity.MY_ID, clk, tag + ticket);

        MainActivity.LIVE_ORDER = true;

        Log.d("CONFIRM", "DONE");

        //toaster for success
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Order submitted successfully!",
                        Toast.LENGTH_LONG).show();
            }
        });

        //switch to MENU
        Intent myIntent = new Intent(this, Main2Activity.class);
        startActivity(myIntent);

    }

    public String serializeTicket (HashMap<String, Integer> t) {

        String out="";
        for( Map.Entry<String, Integer> entry : t.entrySet()) {
            out += entry.getKey()+"="+entry.getValue()+"#";
        }
        return out;
    }


    public void broadcastPeers(Integer[] clk) {

        Log.d("PEER_BRDCAST", "Starting broadcast of clk "+ clk);

        for (int i = 0; i < clk.length; i++) {
            if (i+1 == MainActivity.MY_ID) { continue; }
            else if (MainActivity.IP_MAP[i].equals(MainActivity.MY_IP)) { continue; } //TO BE REMOVED
            else if (MainActivity.IP_MAP[i].equals("0"))  { continue; }
            new Thread(new PeerMsg( MainActivity.IP_MAP[i], clk )).start();
        }
        Log.d("PEER_BRDCAST", "Done");
    }
}

class PeerMsg implements Runnable {

    String remoteIP;
    Integer[] clk;

    public PeerMsg(String ip , Integer c[]){
        this.remoteIP = ip;
        this.clk = c;
    }

    public void run() {

        Socket sckt;
        String OUT = MainActivity.MY_ID.toString() + "!!" + Arrays.toString(clk);

        try {
            sckt = new Socket(remoteIP, MainActivity.CLIENT_PORT);
            PrintWriter outToPeer = new PrintWriter(sckt.getOutputStream(), true);

            outToPeer.println(OUT);
            sckt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("PEER_BRDCAST", "IOException caught " + e.toString() + ". Dest:" + remoteIP);
        }
    }
}

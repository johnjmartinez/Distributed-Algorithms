package com.example.darosale.distributedorderingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TableInfo extends AppCompatActivity {

    public static String user = "default";
    public static String table = "table0";
    public static String status = "offline";
    public static Menu menu1;

    Refresher r = new Refresher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Get the message string that was passed in from the TableLayout/Queue activity
        Intent intent = getIntent();
        table = intent.getStringExtra(TableLayout.EXTRA_MESSAGE);
        setTitle(MyActivity.user + ": " + table.toUpperCase());
        // Update the display info based on the table name passed in
        updateActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.table_menu, menu);
        this.menu1 = menu;
        MenuItem itm = (MenuItem) menu.findItem(R.id.status);
        // If the client is online, set his light to green
        if (MyActivity.tableIPs[Integer.parseInt(table.split("table")[1])-1]!="0") {
            itm.setIcon(R.drawable.online);
            this.status = "online";
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.messages) {
            showMessages();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (MyActivity.tableIPs[Integer.parseInt(table.split("table")[1])-1]!="0") {
            MenuItem itm = (MenuItem) menu.findItem(R.id.status);
            itm.setIcon(R.drawable.online);
            this.status = "online";
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        r.cancel(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        r.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActivity();
    }

    public void updateActivity() {
        r.cancel(true);
        r = new Refresher();
        r.execute();

        // Method for updating each individual views in this activity
        TextView order = (TextView) findViewById(R.id.order1);
        TextView qty = (TextView) findViewById(R.id.qty1);
        TextView foodCost = (TextView) findViewById(R.id.food2);
        TextView compsDisc = (TextView) findViewById(R.id.comps2);
        TextView taxCost = (TextView) findViewById(R.id.tax2);
        TextView cost = (TextView) findViewById(R.id.total2);
        String formatItems;
        String formatQty;
        double price = 0;
        int comps = 0;
        HashMap<String, Integer> x = MyActivity.tableOrders.get(table);
        // Check if the table order does not exist or has no items
        if (!MyActivity.tableOrders.containsKey(table)) {
            // Set empty strings and 0's
            formatItems = "";
            formatQty = "";
            price = 0;
        }
        else if(MyActivity.tableOrders.get(table).isEmpty()){
            // Set empty strings and 0's
            formatItems = "";
            formatQty = "";
            price = 0;
        }
        else{
            // Iterate through the order HashMap and create a parsable string
            String formatOrder = formatItemsAndQty(x);
            // Parse the string for the order items
            formatItems = formatOrder.split("###")[0];
            // Parse the string for the order item quantities
            formatQty = formatOrder.split("###")[1];
            // Calculate the price of the order based on the items and their quantity
            price = calculateTotal(formatItems, formatQty);
        }
        // Check if the table has any comps and get the amount if it does
        if (MyActivity.tableComps.containsKey(table)) {
            comps = MyActivity.tableComps.get(table);
        }
        // Set the order item(s) in the activity
        order.setText(formatItems);
        // Set the order item quantity(s) in the activity
        qty.setText(formatQty);
        // Set the price to 2 decimal places
        double p = Math.round(price * 100) / 100.00;
        foodCost.setText("" + p);
        // Set teh comps to 2 decimal places
        double c = Math.round(price * (comps/100.00) * 100) / 100.00;
        compsDisc.setText("" + c);
        // Set the tax to 2 decimal places
        double t = Math.round(.0825 * (price - c) * 100) / 100.00;
        taxCost.setText("" + t);
        // Add up the numbers to get the total cost of the table order
        double tot = Math.round((price - c + t) * 100.00) / 100.00;
        cost.setText("$" + tot);
        // Set the status light
        if (status.equals("online")){
            onPrepareOptionsMenu(menu1);
        }
        else if (status.equals("offline")){
            onPrepareOptionsMenu(menu1);
        }
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

    public double calculateTotal(String items, String qty){
        // Method for calculating the total price of the order
        double total = 0;
        double price = 0;
        int quantity = 0;
        // Split the items up into an array
        String[] itms = items.split("\n");
        // Split the quantities up into an array
        String[] qtys = qty.split("\n");
        // Loop through the arrays
        for (int i=0; i<itms.length; i++){
            // Get the price of the item
            price = MyActivity.getItemPrice(itms[i]);
            // Parse the quantity to an integer
            quantity = Integer.parseInt(qtys[i]);
            // Update the total with the product of the item price and item quantity
            total += price*quantity;
        }
        return total;
    }

    public String[] getItemList(){
        // Method for getting the list of item available items and prices
        HashMap<String, Double> prices = MyActivity.PRICES;
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
        if (status.equals("offline")){
            return;
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TableInfo.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select Item to Add");
        // Use a single choice list for our dialogue
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                TableInfo.this,
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
                AlertDialog.Builder builderInner = new AlertDialog.Builder(TableInfo.this);
                builderInner.setTitle("Add " + strName + "?");
                // If the selection is confirmed, we need to handle special cases
                builderInner.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Check if the order was previously placed
                        if (MyActivity.tableOrders.containsKey(table)){
                            // Check if the order already has this item in it
                            if (MyActivity.tableOrders.get(table).containsKey(strName)){
                                // If the item was already there, we need to update the quantity
                                int val = MyActivity.tableOrders.get(table).get(strName);
                                MyActivity.updateTableOrder(table, strName, val + 1);
                            }
                            else {
                                // If the item wasn't there, we just add a single item to the order
                                MyActivity.updateTableOrder(table, strName, 1);
                            }
                        }
                        else {
                            // If the order didn't exist, we need to create it
                            MyActivity.updateTableOrder(table, strName, 1);
                        }
                        String cmd = "6000!!" + Arrays.toString(MyActivity.vClock) +
                                "!!UPDATE!!" + strName + "=1";
                        String ip = MyActivity.tableIPs[Integer.parseInt(table.split("table")[1])-1];
                        // Send the update to the table
                        ListenerThread.TCPCall(ip, cmd);
                        // Update the display info with the new table order
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

    public void deleteFromOrder(View view){
        // Method for handling the popup dialogue for deleting an order item
        // Do not create popup if the order is empty
        if (status.equals("offline") || !MyActivity.tableOrders.containsKey(table) ||
                MyActivity.tableOrders.get(table).isEmpty()){
            return;
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TableInfo.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select Item to Delete");
        // Use a single choice list for our dialogue
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                TableInfo.this,
                android.R.layout.select_dialog_singlechoice);
        // Get a string representation of the items and quantity of the order
        String itemsAndQty = formatItemsAndQty(MyActivity.tableOrders.get(table));
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
                AlertDialog.Builder builderInner = new AlertDialog.Builder(TableInfo.this);
                builderInner.setTitle("Delete " + strName + "?");
                // If the delete was confirmed, we need to handle special cases
                builderInner.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int val = MyActivity.tableOrders.get(table).get(strName);
                        // Check if the value of the item being deleted is 1
                        if (val == 1) {
                            // Remove the item from the table so that we do not display it again
                            MyActivity.tableOrders.get(table).remove(strName);
                        } else {
                            // The item quantity was more than 1 so we can just seubtract
                            MyActivity.tableOrders.get(table).put(strName, val - 1);
                        }
                        // Check to see if there are any items left in the order
                        if (MyActivity.tableOrders.get(table).isEmpty()) {
                            // Set comps to 0
                            MyActivity.tableComps.put(table, 0);
                        }
                        String cmd = "6000!!" + Arrays.toString(MyActivity.vClock) +
                                "!!UPDATE!!" + strName + "=-1";
                        String ip = MyActivity.tableIPs[Integer.parseInt(table.split("table")[1]) - 1];
                        // Send the update to the table
                        ListenerThread.TCPCall(ip, cmd);
                        // Update the display info with the new table order
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

    public void addComp(View view){
        // Method for handling the popup dialogue for adding a comp
        if (status.equals("offline") || !MyActivity.tableOrders.containsKey(table) ||
                MyActivity.tableOrders.get(table).isEmpty()){
            return;
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TableInfo.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Add Comp");
        // Set the dialogue view to an EditText view
        final EditText input = new EditText(this);
        // Allow only numbers as input
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        input.setHint("0-100");
        input.setHintTextColor(Color.parseColor("#FFFFECF8"));
        builderSingle.setMessage("Enter a percentage amount");
        builderSingle.setView(input);
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // If the comp is confirmed, check to see if it is less than 100
                int val = Integer.parseInt(input.getText().toString());
                if (val > 100) {
                    return;
                }
                // Add the comp to the tables comp property
                MyActivity.tableComps.put(table, val);
                // Update the display info with the new comp
                updateActivity();
            }
        });
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    public void cashOut(View view){
        // Method for handling an order settlement
        if (status.equals("offline")){
            return;
        }
        // Remove the table comp
        MyActivity.tableComps.remove(table);
        // Remove the table order
        MyActivity.tableOrders.remove(table);
        // Clear the display of the table order
        updateActivity();
        String cmd = "6000!!" + Arrays.toString(MyActivity.vClock) + "!!CLEAR!!null";
        String ip = MyActivity.tableIPs[Integer.parseInt(table.split("table")[1])-1];
        // Send the clear call to table
        ListenerThread.TCPCall(ip, cmd);
        MyActivity.messages.add("Payment Received for " + table);
        removeFromQueue();
    }

    public void removeFromQueue(){
        // Method for removing an item from the queue
        for (int i=1; i<11; i++){
            if (MyActivity.queue.get(i).split("#")[0].equals(table)){
                for (int j=i+1; j<11; j++){
                    MyActivity.queue.put(i, MyActivity.queue.get(j));
                    if (j==10){
                        MyActivity.queue.put(j, "Empty");
                    }
                }
            }
        }
    }

    public void showMessages(){
        // Method for handling popup dialogue displaying messages
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TableInfo.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Messages");
        // Use a single choice list for our dialogue
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                TableInfo.this,
                android.R.layout.simple_list_item_1);
        // Add each item to the adapter for the list
        for (int i=MyActivity.messages.size()-1; i>=0; i--) {
            arrayAdapter.add(MyActivity.messages.get(i));
        }
        // If the delete is canceled, just exit
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // no-op
            }
        });
        // If an item was selected, create a second popup to confirm
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builderSingle.show();
    }

    private class Refresher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            while (!Thread.currentThread().isInterrupted()  ) {
                try {
                    if (MyActivity.getRefreshStatus()) { break; }
                    if ( isCancelled() ) { return null; }
                    Thread.sleep(2000);
                }
                catch (InterruptedException ie) {
                    //Thread got cancelled.
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (MyActivity.getRefreshStatus()) {
                MyActivity.unsetRefresh();
                updateActivity();
            }
            super.onPostExecute(result);
        }
    }
}
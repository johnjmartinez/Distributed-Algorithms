package com.example.darosale.distributedorderingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MyActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.darosale.distributedorderingsystem.MESSAGE";
    public static final HashMap<String, Double> PRICES;
    public static final int serverPort = 6000;
    public static final int clientPort = 1234;

    static {
        PRICES = new HashMap<String, Double>();
        PRICES.put("NEW THE BLAZIN' TEXAN", 15.99);
        PRICES.put("NEW ALL-DAY BRUNCH BURGER", 10.99);
        PRICES.put("7OZ GRILLED ONION SIRLOIN WITH STOUT GRAVY", 17.99);
        PRICES.put("HOT SHOT WHISKEY CHICKEN", 14.99);
        PRICES.put("SHRIMP WONTON STIR-FRY", 14.99);
        PRICES.put("TRIPLE CHOCOLATE MELTDOWN", 7.99);
        PRICES.put("CHICKEN CEASAR SALAD", 9.99);
        PRICES.put("ORIENTAL CHICKEN SALAD", 9.99);
        PRICES.put("FRESH FRUIT CITRONADE", 6.99);
        PRICES.put("HOT FUDGE SUNDAE DESSERT SHOOTER", 7.99);
    }

    public static String user = "default";
    public static HashMap<String, Integer> tableComps = new HashMap<String, Integer>();
    public static HashMap<String, HashMap<String, Integer>> tableOrders = new HashMap<String, HashMap<String, Integer>>();
    public static HashMap<String, String> accounts = new HashMap<String, String>();
    public static int[] vClock = {0,0,0,0,0,0,0,0,0,0};
    public static ArrayList<String> messages = new ArrayList<String>();
    public static String[] tableIPs = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    private static boolean refreshView = false;

    static {
        accounts.put("root", "pass");
    }

    public static HashMap<Integer, String> queue = new HashMap<Integer, String>();

    static {
        queue.put(1, "Empty");
        queue.put(2, "Empty");
        queue.put(3, "Empty");
        queue.put(4, "Empty");
        queue.put(5, "Empty");
        queue.put(6, "Empty");
        queue.put(7, "Empty");
        queue.put(8, "Empty");
        queue.put(9, "Empty");
        queue.put(10, "Empty");
    }


    public static double getItemPrice(String item) {
        // Method for returning the price of an item
        return PRICES.get(item);
    }

    public static synchronized void updateTableOrder(String table, String item, Integer qty) {
        // Method for creating, or updating table orders
        // Check if the table order is already there, we can just update it
        // Else we need to instantiate the mapping and the add the item
        // We synchronize because the listener thread and main thread both
        // make calls here
        if (tableOrders.containsKey(table)) {
            tableOrders.get(table).put(item, qty);
        } else {
            HashMap<String, Integer> newTable = new HashMap<String, Integer>();
            newTable.put(item, qty);
            tableOrders.put(table, newTable);
        }
    }

    public static HashMap<String, Integer> getTableOrder(String table) {
        // Method for returning a tables order
        return tableOrders.get(table);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("JDA Restaurant");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Create the TCP listener thread and start it
        ListenerThread lt = new ListenerThread(getApplication());
        lt.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
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
    }

    public void login(View view) {
        // Method for handling the login of a user
        EditText usr = (EditText) findViewById(R.id.username1);
        String setUser = usr.getText().toString();
        EditText pwd = (EditText) findViewById(R.id.pwd1);
        String passwd = pwd.getText().toString();
        TextView invalid = (TextView) findViewById(R.id.invalid1);
        // Check if the user is authorized
        if (accounts.containsKey(setUser)) {
            // Check if the password matches the user profile
            if (passwd.equals(accounts.get(setUser))) {
                invalid.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(this, TableLayout.class);
                user = setUser;
                startActivity(intent);
            }
        } else {
            // Display a message to the user about failed authentication
            invalid.setVisibility(View.VISIBLE);
        }
    }

    public void createAccount(View view) {
        // Method for creating a new user account through a popup is generated for user input
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MyActivity.this);
        builderSingle.setTitle("Create User Account");
        //builderSingle.setIcon(R.drawable.ic_launcher);
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate the dialogue xml
        final View inflaterView = inflater.inflate(R.layout.create_account, null);
        // Set the inflater to the dialogue
        builderSingle.setView(inflaterView);
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText u = (EditText) inflaterView.findViewById(R.id.username2);
                EditText p = (EditText) inflaterView.findViewById(R.id.password2);
                String usr = u.getText().toString();
                String pwd = p.getText().toString();
                // Add the user to the account if confirmed
                accounts.put(usr, pwd);
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    synchronized public static void setRefresh() {
        refreshView = true;
    }

    synchronized public static void unsetRefresh() {
        refreshView = false;
    }

    synchronized public static Boolean getRefreshStatus() {
        return refreshView;
    }
}

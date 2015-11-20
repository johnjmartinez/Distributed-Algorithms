package com.example.darosale.distributedorderingsystem;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class MyActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.darosale.distributedorderingsystem.MESSAGE";
    public static HashMap<String, HashMap<String, Integer>> tableOrders =
            new HashMap<String, HashMap<String, Integer>>();

    public static void updateTableOrder(String table, String item, Integer qty){
        if (tableOrders.containsKey(table)){
            tableOrders.get(table).put(item, qty);
        }
        else {
            HashMap<String, Integer> newTable = new HashMap<String, Integer>();
            newTable.put(item, qty);
            tableOrders.put(table, newTable);
        }
    }

    public static HashMap<String, Integer> getTableOrder(String table){
        return tableOrders.get(table);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
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

    public void login(View view){
        EditText usr = (EditText) findViewById(R.id.username1);
        String user = usr.getText().toString();
        EditText pwd = (EditText) findViewById(R.id.pwd1);
        String passwd = pwd.getText().toString();
        TextView invalid = (TextView) findViewById(R.id.invalid1);
        if (user.equals("david") && passwd.equals("rosales")) {
            invalid.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, TableLayout.class);
            intent.putExtra(EXTRA_MESSAGE, user);
            startActivity(intent);
        }
        else {
            invalid.setVisibility(View.VISIBLE);
        }
    }
}

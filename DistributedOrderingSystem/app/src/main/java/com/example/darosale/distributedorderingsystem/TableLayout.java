package com.example.darosale.distributedorderingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TableLayout extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.darosale.distributedorderingsystem.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Welcome " + MyActivity.user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.messages) {
            showMessages();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewTable(View view){
        // Method for switching to the TableInfo activity
        String table = "default";
        // Store the table name that is clicked as a string
        switch (view.getId()) {
            case R.id.table1:
                table = "table1";
                break;
            case R.id.table2:
                table = "table2";
                break;
            case R.id.table3:
                table = "table3";
                break;
            case R.id.table4:
                table = "table4";
                break;
            case R.id.table5:
                table = "table5";
                break;
            case R.id.table6:
                table = "table6";
                break;
            case R.id.table7:
                table = "table7";
                break;
            case R.id.table8:
                table = "table8";
                break;
            case R.id.table9:
                table = "table9";
                break;
            case R.id.table10:
                table = "table10";
                break;
        }
        Intent intent = new Intent(this, TableInfo.class);
        // Pass the table clicked to the TableInfo class as a string
        intent.putExtra(EXTRA_MESSAGE, table);
        startActivity(intent);
    }

    public void viewOrders(View view){
        // Method for switching to the Queue activity
        Intent intent = new Intent(this, Queue.class);
        startActivity(intent);
    }

    public void showMessages(){
        // Method for handling popup dialogue displaying messages
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TableLayout.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Messages");
        // Use a single choice list for our dialogue
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                TableLayout.this,
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
}

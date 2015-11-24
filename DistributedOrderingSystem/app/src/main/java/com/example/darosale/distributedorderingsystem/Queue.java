package com.example.darosale.distributedorderingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Queue extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.darosale.distributedorderingsystem.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set the queue list
        setListView();
    }

    public void setListView(){
        // Method for setting the order queue listView
        final ListView l = (ListView) findViewById(R.id.orderList);
        String[] queueItems = new String[10];
        String table = "table0";
        String order = "Empty";
        // Loop through the order queue
        for (int i=1; i<11; i++){
            // Get the table order for this queue spot
            table = MyActivity.queue.get(i);
            // Format the queue entry
            order = i + ". " + table.toUpperCase();
            // Add the entry to a list
            queueItems[i-1] = order;
        }
        // Create the adapter for the queue listView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, queueItems);
        l.setAdapter(adapter);
        // Create a listener for clicks on list items
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the position value of the item clicked on
                String val = (String) l.getItemAtPosition(position);
                // Get the queue entry for that position
                String table = val.split(" ")[1].split("#")[0].toLowerCase();
                // Check if the entry is not "Empty"
                if (!table.equals("Empty")) {
                    // Go to the TableInfo activity
                    goToTable(table);
                }
            }
        });
    }

    public void goToTable(String table){
        // Method for ceating the TableInfo activity
        Intent intent = new Intent(this, TableInfo.class);
        // Pass the table as a string to the new activity
        intent.putExtra(EXTRA_MESSAGE, table);
        startActivity(intent);
    }
}

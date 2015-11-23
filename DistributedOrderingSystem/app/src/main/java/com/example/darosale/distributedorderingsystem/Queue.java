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
        setListView();
    }

    public void setListView(){
        final ListView l = (ListView) findViewById(R.id.orderList);
        String[] queueItems = new String[10];
        String table = "table0";
        String order = "Empty";
        for (int i=1; i<11; i++){
            table = MyActivity.queue.get(i);
            order = i + ". " + table.toUpperCase();
            queueItems[i-1] = order;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, queueItems);
        l.setAdapter(adapter);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String val = (String) l.getItemAtPosition(position);
                String table = val.split(" ")[1].toLowerCase();
                if (!table.equals("empty")) {
                    goToTable(table);
                }
            }
        });
    }

    public void goToTable(String table){
        Intent intent = new Intent(this, TableInfo.class);
        intent.putExtra(EXTRA_MESSAGE, table);
        startActivity(intent);
    }
}

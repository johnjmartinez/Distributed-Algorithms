package com.example.darosale.distributedorderingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Queue extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.darosale.distributedorderingsystem.MESSAGE";
    Refresher r = new Refresher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set the queue list
        setListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.queue_menu, menu);

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
        setListView();
    }

    public void setListView(){
        r.cancel(true);
        r = new Refresher();
        r.execute();

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

    public void showMessages(){
        // Method for handling popup dialogue displaying messages
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Queue.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Messages");
        // Use a single choice list for our dialogue
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                Queue.this,
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
                    if (isCancelled() ) { return null; }
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
                setListView();
            }
            super.onPostExecute(result);
        }
    }
}

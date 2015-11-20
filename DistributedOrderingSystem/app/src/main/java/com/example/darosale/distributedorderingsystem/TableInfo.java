package com.example.darosale.distributedorderingsystem;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TableInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_info);
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
        TextView currentOrder = (TextView) findViewById(R.id.order1);
        MyActivity.updateTableOrder("table1", "chicken and waffles", 1);
        MyActivity.updateTableOrder("table1", "steak and potatos", 6);
        MyActivity.updateTableOrder("table1", "burger and fries", 9);
        HashMap<String, Integer> x = MyActivity.getTableOrder("table1");
        String formatOrder = this.formatItemsAndQty(x);
        String formatItems = formatOrder.split("###")[0];
        String formatQty = formatOrder.split("###")[1];
        currentOrder.setText(formatItems);
        currentOrder.setTextColor(Color.BLUE);
    }

    public String formatItemsAndQty(HashMap<String, Integer> mp){
        String items = "";
        String qty = "";
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            items = items + pair.getKey() + "\n";
            qty = qty + pair.getValue() + "\n";
            it.remove();
        }
        return items + "###" + qty;
    }

}

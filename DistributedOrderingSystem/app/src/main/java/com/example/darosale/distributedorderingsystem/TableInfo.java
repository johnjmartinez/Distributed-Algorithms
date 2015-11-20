package com.example.darosale.distributedorderingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
        MyActivity.updateTableOrder("table1", "Chicken & Waffles", 1);
        MyActivity.updateTableOrder("table1", "Steak & Potatoes", 1);
        MyActivity.updateTableOrder("table1", "Burger & Fries", 2);
        updateActivity();
    }

    public void updateActivity(){
        TextView order = (TextView) findViewById(R.id.order1);
        TextView qty = (TextView) findViewById(R.id.qty1);
        TextView cost = (TextView) findViewById(R.id.cost1);
        String formatItems;
        String formatQty;
        double total;
        HashMap<String, Integer> x = MyActivity.tableOrders.get("table1");
        if (MyActivity.tableOrders.get("table1").isEmpty()){
            formatItems = "";
            formatQty = "";
            total = 0;
        }
        else {
            String formatOrder = this.formatItemsAndQty(x);
            formatItems = formatOrder.split("###")[0];
            formatQty = formatOrder.split("###")[1];
            total = this.calculateTotal(formatItems, formatQty);
        }
        order.setText(formatItems);
        qty.setText(formatQty);
        cost.setText("$" + total);
    }

    public String formatItemsAndQty(HashMap<String, Integer> mp){
        String items = "";
        String qty = "";
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            items += pair.getKey();
            qty += pair.getValue();
            if (it.hasNext()) {
                items += "\n";
                qty += "\n";
            }
        }
        return items + "###" + qty;
    }

    public Double calculateTotal(String items, String qty){
        String[] itms = items.split("\n");
        String[] qtys = qty.split("\n");
        double total = 0;
        double price = 0;
        int quantity = 0;
        for (int i=0; i<itms.length; i++){
            price = MyActivity.getItemPrice(itms[i]);
            quantity = Integer.parseInt(qtys[i]);
            total += price*quantity;
        }
        return total;
    }

    public void deleteList(View view) {
        if (MyActivity.tableOrders.get("table1").isEmpty()){
            return;
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TableInfo.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select Item to Delete");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                TableInfo.this,
                android.R.layout.select_dialog_singlechoice);
        String itemsAndQty = formatItemsAndQty(MyActivity.tableOrders.get("table1"));
        String[] items = itemsAndQty.split("###")[0].split("\n");
        for (int i = 0; i < items.length; i++) {
            arrayAdapter.add(items[i]);
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(TableInfo.this);
                builderInner.setTitle("Delete " + strName + "?");
                builderInner.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int val = MyActivity.tableOrders.get("table1").get(strName);
                        if (val==1){
                            MyActivity.tableOrders.get("table1").remove(strName);
                        }
                        else {
                            MyActivity.tableOrders.get("table1").put(strName, val - 1);
                        }
                        updateActivity();
                    }
                });
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

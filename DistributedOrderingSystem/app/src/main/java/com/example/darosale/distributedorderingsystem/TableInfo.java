package com.example.darosale.distributedorderingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
        MyActivity.updateTableOrder("table1", "Chicken & Waffles", 1);
        MyActivity.updateTableOrder("table1", "Steak & Potatoes", 1);
        MyActivity.updateTableOrder("table1", "Burger & Fries", 2);
        updateActivity();
    }

    public void updateActivity() {
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
        HashMap<String, Integer> x = MyActivity.tableOrders.get("table1");
        if (MyActivity.tableOrders.get("table1").isEmpty()) {
            formatItems = "";
            formatQty = "";
            price = 0;
        } else {
            String formatOrder = formatItemsAndQty(x);
            formatItems = formatOrder.split("###")[0];
            formatQty = formatOrder.split("###")[1];
            price = calculateTotal(formatItems, formatQty);
        }
        if (MyActivity.tableComps.containsKey("table1")) {
            comps = MyActivity.tableComps.get("table1");
        }
        order.setText(formatItems);
        qty.setText(formatQty);
        double p = Math.round(price * 100) / 100.00;
        foodCost.setText("" + p);
        double c = Math.round(price * (comps/100.00) * 100) / 100.00;
        compsDisc.setText("" + c);
        double t = Math.round(.0825 * (price - c) * 100) / 100.00;
        taxCost.setText("" + t);
        double tot = Math.round((price - c + t) * 100.00) / 100.00;
        cost.setText("$" + tot);
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

    public double calculateTotal(String items, String qty){
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

    public String[] getItemList(){
        HashMap<String, Double> prices = MyActivity.PRICES;
        Iterator it = prices.entrySet().iterator();
        String items = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            items += pair.getKey() + "#";
            }
        return items.split("#");
    }

    public void addToOrder(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TableInfo.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select Item to Delete");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                TableInfo.this,
                android.R.layout.select_dialog_singlechoice);
        String[] items = getItemList();
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
                builderInner.setTitle("Add " + strName + "?");
                builderInner.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (MyActivity.tableOrders.containsKey("table1")){
                            if (MyActivity.tableOrders.get("table1").containsKey(strName)){
                                int val = MyActivity.tableOrders.get("table1").get(strName);
                                MyActivity.updateTableOrder("table1", strName, val+1);
                            }
                            else {
                                MyActivity.updateTableOrder("table1", strName, 1);
                            }
                        }
                        else {
                            MyActivity.updateTableOrder("table1", strName, 1);
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

    public void deleteFromOrder(View view){
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

    public void addComp(View view){
        if (MyActivity.tableOrders.get("table1").isEmpty()) {
            return;
        }
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TableInfo.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Add Comp");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        input.setMaxWidth(100);
        input.setTextAlignment(4);
        input.setHint("0-100");
        input.setHintTextColor(Color.parseColor("#FFFFECF8"));
        builderSingle.setTitle("Add Comp");
        builderSingle.setMessage("Enter a percentage amount");
        builderSingle.setView(input);
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int val = Integer.parseInt(input.getText().toString());
                if (val > 100){
                    return;
                }
                MyActivity.tableComps.put("table1", val);
                Log.d("put", "" + MyActivity.tableComps.get("table1"));
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
    }

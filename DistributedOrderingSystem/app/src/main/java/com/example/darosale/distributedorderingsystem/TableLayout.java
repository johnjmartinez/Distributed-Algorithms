package com.example.darosale.distributedorderingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Welcome " + MyActivity.user);
    }

    public void viewTable(View view){
        String table = "default";
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
        intent.putExtra(EXTRA_MESSAGE, table);
        startActivity(intent);
    }

    public void viewOrders(View view){
        Intent intent = new Intent(this, Queue.class);
        startActivity(intent);
    }
}

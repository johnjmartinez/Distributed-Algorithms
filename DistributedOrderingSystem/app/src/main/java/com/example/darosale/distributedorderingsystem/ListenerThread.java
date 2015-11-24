package com.example.darosale.distributedorderingsystem;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by darosale on 11/23/2015.
 */
public class ListenerThread extends Thread {

    public final int port = 6000;

    public void run() {
        Log.d("ListenerThread run()", "Check 1 Thread started");
        String msg = "";
        String[] data;
        Log.d("ListenerThread run()", "Check 2 Starting TestThread");
        TestThread t = new TestThread();
        t.start();
        try {
            Log.d("ListenerThread run()", "Check 3 Entering while loop");
            while (true) {
                Log.d("ListenerThread run()", "Check 4 Creating socket");
                ServerSocket srv = new ServerSocket(port);
                Log.d("ListenerThread run()", "Check 5 Opening socket");
                Socket sock = srv.accept();
                Log.d("ListenerThread run()", "Check 6 Creating output stream");
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                Log.d("ListenerThread run()", "Check 7 Creating input stream");
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                out.println("Connected");
                while (!in.ready()) {
                    //wait
                }
                Log.d("ListenerThread run()", "Check 8 Reading message");
                msg = in.readLine();
                Log.d("ListenerThread run()", "Check 9 Parsing message");
                data = msg.split("###");
                if (data[1].equals("Order")){
                    acceptOrder(data);
                }
                out.println("Order accepted");
                Log.d("ListenerThread run()", "Check 10 Transaction complete, closing");
                out.close();
                Log.d("ListenerThread run()", "Check 11");
                in.close();
                Log.d("ListenerThread run()", "Check 12");
                sock.close();
                Log.d("ListenerThread run()", "Check 13");
                srv.close();
                Log.d("ListenerThread run()", "Check 14");

            }
        } catch (Exception exc) {
            System.out.println("Caught exception: " + exc);
        }
    }

    public void acceptOrder(String[] data){
        // Method for updating table orders on receiving a client order request
        String item = "";
        int qty = 0;
        Log.d("acceptOrder", "Check 1");
        // Split the order up into individual item/quantity's
        String[] items = data[2].split("#");
        // Loop through the items and updating the table orders
        Log.d("acceptOrder", "Check 2");
        for (int i=0; i<items.length; i++) {
            Log.d("acceptOrder", "Check 2a");
            item = items[i].split("%")[0];
            qty = Integer.parseInt(items[i].split("%")[1]);
            MyActivity.updateTableOrder(data[0], item, qty);
        }
        // Update the order of the queue
        Log.d("acceptOrder", "Check 3");
        placeOrderInQueue(data[3].split(","), data[0]);
        Log.d("acceptOrder", "Check 4");
    }

    public void placeOrderInQueue(String[] clock, String table){
        // Method for adding an order to the proper slot in the queue
        Log.d("placeOrderInQueue", "Check 1");
        int relVal = Integer.parseInt(table.split("table")[1])-1;
        Log.d("placeOrderInQueue", "Check 2 relVal: " + relVal);
        int iter = 1;
        Log.d("Check", "" + Integer.parseInt(clock[relVal]));
        Log.d("Check", "" + Integer.parseInt(MyActivity.vClock.split(",")[relVal]));
        if (Integer.parseInt(clock[relVal]) <= Integer.parseInt(
                MyActivity.vClock.split(",")[relVal])){
            Log.d("placeOrderInQueue", "Check 3");
            for (int i=1; i<11; i++){
                Log.d("placeOrderInQueue", "Check 4");
                if (Integer.parseInt(clock[relVal]) <=
                        Integer.parseInt(MyActivity.queue.get(i).split("#")[1].split(",")[relVal])){
                    Log.d("placeOrderInQueue", "Check 5");
                    String tmp = MyActivity.queue.get(i);
                    Log.d("placeOrderInQueue", "Check 6");
                    shiftOrders(i+1, tmp);
                    Log.d("placeOrderInQueue", "Check 7");
                    MyActivity.queue.put(i, table + "#" + TextUtils.join(",", clock));
                    Log.d("placeOrderInQueue", "Check 8");
                    updateClock(clock);
                    break;
                }
            }
        }
        else {
            for (int i=1; i<11; i++){
                if (MyActivity.queue.get(i).equals("Empty")){
                    MyActivity.queue.put(i, table + "#" + TextUtils.join(",", clock));
                    updateClock(clock);
                    break;
                }
            }
        }
    }

    public void orderQueue(String[] clock, String table){
        // Method for adding an order to the proper spot in the order queue
        int relVal = Integer.parseInt(table.split("table")[1]);
        Log.d("orderQueue", "Check 1 relVal: " + relVal);
        int iter = 1;
        String[] qClock;
        Log.d("orderQueue", "Check 2 clock: " + TextUtils.join(",", clock));
        Log.d("orderQueue", "Check 3 table: " + table);
        // Loop to find and place the new order where it needs to go
        Log.d("orderQueue", "Check 4");
        for (int i=1; i<11; i++){
            Log.d("orderQueue", "Check 5");
            // If the queue order is "Empty" we can just place the order
            if (MyActivity.queue.get(i).equals("Empty")){
                Log.d("orderQueue", "Check 6 Queue empty");
                MyActivity.queue.put(i, table + "#" + TextUtils.join(",", clock));
                Log.d("orderQueue", "Check 7 Order in queue");
                break;
            }
            // Get the vector clock of the order in the queue
            Log.d("orderQueue", "Check 8");
            qClock = MyActivity.queue.get(i).split("#")[1].split(",");
            Log.d("orderQueue", "Check 9 order " + i + " clock: " + TextUtils.join(",", qClock));
            // If the order vector clock component is not greater than this clocks, the order
            // was placed before the one in the queue
            Log.d("orderQueue", "Check 10");
            if(Integer.parseInt(clock[relVal]) <= Integer.parseInt(qClock[relVal])){
                // Create a temporary order for the order in this queue spot
                Log.d("orderQueue", "Check 11");
                String tmp = MyActivity.queue.get(i);
                // This order belongs here so place it in this queue spot
                Log.d("orderQueue", "Check 12 tmp: " + tmp);
                MyActivity.queue.put(i, table + "#" + TextUtils.join(",", clock));
                // Shift the rest of the orders down one spot in the queue
                Log.d("orderQueue", "Check 13");
                shiftOrders(i+1, tmp);
                break;
            }
            continue;
        }
        Log.d("orderQueue", "Check 14");
        // Update the global vector clock
        updateClock(clock);
        Log.d("orderQueue", "Check 15");
    }

    public void shiftOrders(int queuePlace, String tmp){
        // Method for shifting the order of the queue to accommodate an earlier order
        int lastOrder = 9;
        // Find the last order in the queue
        for (int i=queuePlace; i<10; i++){
            // If the queue spot is empty, the last order was the last run through the loop
            if (MyActivity.queue.get(i).equals("Empty")) {
                lastOrder = i-1;
                break;
            }
        }
        // Move the orders 1 spot down the queue
        for (int i=lastOrder; i>queuePlace; i--){
            MyActivity.queue.put(i, MyActivity.queue.get(i-1));
        }
        // Put the tmp order where it should be in the queue
        MyActivity.queue.put(queuePlace, tmp);
    }

    public void updateClock(String[] clock){
        // Method for updating the vector clock of the system
        String[] newClock = new String[10];
        String[] vc = MyActivity.vClock.split(",");
        // Compare each vector component and take the max
        for (int i=0; i<10; i++){
            if (Integer.parseInt(vc[i]) > Integer.parseInt(clock[i])){
                newClock[i] = vc[i];
            }
            else {
                newClock[i] = clock[i];
            }
        }
        // Set the vector clock
        MyActivity.vClock = TextUtils.join(",", newClock);
        Log.d("Check", "Vector Clock: " + MyActivity.vClock);
    }
}

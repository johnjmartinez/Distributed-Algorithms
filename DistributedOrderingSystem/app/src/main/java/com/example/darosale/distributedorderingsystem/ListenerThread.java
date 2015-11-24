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
 *
 * A Thread which listens for incoming TCP requests from clients and handles them accordingly
 *
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
        // Loop forever listening for incoming requests
        try {
            Log.d("ListenerThread run()", "Check 3 Entering while loop");
            while (true) {
                Log.d("ListenerThread run()", "Check 4 Creating socket");
                // Create the server socket to listen on
                ServerSocket srv = new ServerSocket(port);
                Log.d("ListenerThread run()", "Check 5 Opening socket");
                // Wait for a client request to come in
                Socket sock = srv.accept();
                Log.d("ListenerThread run()", "Check 6 Creating output stream");
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                Log.d("ListenerThread run()", "Check 7 Creating input stream");
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                // When a request is accepted, send an acknowledgement to the client
                out.println("Connected");
                // Wait fo the clients request message
                while (!in.ready()) {
                    //wait
                }
                Log.d("ListenerThread run()", "Check 8 Reading message");
                // Read the clients request message
                msg = in.readLine();
                Log.d("ListenerThread run()", "Check 9 Parsing message");
                data = msg.split("###");
                // Check if the request is tagged with "Order"
                if (data[1].equals("Order")){
                    // Accept the order
                    acceptOrder(data);
                }
                out.println("Order accepted");
                Log.d("ListenerThread run()", "Check 10 Transaction complete, closing");
                // Close the current TCP session
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
            System.out.println("Caught exception while waiting for client request: " + exc);
        }
    }

    public void acceptOrder(String[] data){
        // Method for updating table orders on receiving a client order request
        String item = "";
        int qty = 0;
        Log.d("acceptOrder", "Check 1");
        // Split the order up into individual item/quantity's
        String[] items = data[2].split("#");
        // Loop through the items and update the table orders
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
        // The vector clock component of the table the order is coming from will either be less
        // than, greater than, or equal to the matching server vector clock. If it is greater,
        // our order queue is in a good state and we can place this order at the tail of the
        // queue. If it is less than or equal to the server clock, then another order that was
        // placed after this order made it to the server first. To maintain FIFO, we handle below.
        if (Integer.parseInt(clock[relVal]) <= Integer.parseInt(
                MyActivity.vClock.split(",")[relVal])){
            Log.d("placeOrderInQueue", "Check 3");
            // Loop through the order queue and find the first order that has jumped this order in
            // line. The vector clock will maintain FIFO this way since any order that is processed
            // before an earlier order will get pushed back appropriately.
            for (int i=1; i<11; i++){
                Log.d("placeOrderInQueue", "Check 4");
                // Check if this queue order is out of place
                if (Integer.parseInt(clock[relVal]) <=
                        Integer.parseInt(MyActivity.queue.get(i).split("#")[1].split(",")[relVal])){
                    Log.d("placeOrderInQueue", "Check 5");
                    // Store the order in the slot temporarily to push the queue
                    String tmp = MyActivity.queue.get(i);
                    Log.d("placeOrderInQueue", "Check 6");
                    // Push every order from this point on in the queue back one spot
                    shiftOrders(i+1, tmp);
                    Log.d("placeOrderInQueue", "Check 7");
                    // Place the order in the queue at this spot. This will be the correct spot
                    // unless it also has jumped another order in which case this will get handled
                    // when that order is processed and maintain our FIFO.
                    MyActivity.queue.put(i, table + "#" + TextUtils.join(",", clock));
                    Log.d("placeOrderInQueue", "Check 8");
                    // Update the server vector clock
                    updateClock(clock);
                    break;
                }
            }
        }
        else {
            // The queue is synched properly with the server vector clock so we just find the first
            // empty queue slot to put the order in and update the clock
            for (int i=1; i<11; i++){
                if (MyActivity.queue.get(i).equals("Empty")){
                    MyActivity.queue.put(i, table + "#" + TextUtils.join(",", clock));
                    updateClock(clock);
                    break;
                }
            }
        }
    }

    public void shiftOrders(int queuePlace, String tmp){
        // Method for shifting the order of the queue to accommodate an earlier order
        int lastOrder = 9;
        // Loop through the order queue and find the last order in the queue
        for (int i=queuePlace; i<10; i++){
            // Chekc if the queue spot is empty
            if (MyActivity.queue.get(i).equals("Empty")) {
                // The last order was in the previous run through the loop so take that one
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
        // Loop through each component vector
        for (int i=0; i<10; i++){
            // Check if the vector clock component is greater than the order clock component
            if (Integer.parseInt(vc[i]) > Integer.parseInt(clock[i])){
                // Take the vector clock component
                newClock[i] = vc[i];
            }
            else {
                // Take the order clock component
                newClock[i] = clock[i];
            }
        }
        // Set the vector clock
        MyActivity.vClock = TextUtils.join(",", newClock);
        Log.d("Check", "Vector Clock: " + MyActivity.vClock);
    }

    public void orderQueue(String[] clock, String table){

        /***
         * Faulty process for maintaining messaging FIFO
         ***/

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
            // Check if the queue order is "Empty"
            if (MyActivity.queue.get(i).equals("Empty")){
                Log.d("orderQueue", "Check 6 Queue empty");
                // Place the order
                MyActivity.queue.put(i, table + "#" + TextUtils.join(",", clock));
                Log.d("orderQueue", "Check 7 Order in queue");
                break;
            }
            // Get the vector clock of the order in the queue
            Log.d("orderQueue", "Check 8");
            qClock = MyActivity.queue.get(i).split("#")[1].split(",");
            Log.d("orderQueue", "Check 9 order " + i + " clock: " + TextUtils.join(",", qClock));
            // Check if the order vector clock component is not greater than this clocks, the order
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
}

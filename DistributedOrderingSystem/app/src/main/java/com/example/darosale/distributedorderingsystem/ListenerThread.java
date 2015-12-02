package com.example.darosale.distributedorderingsystem;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by darosale on 11/23/2015.
 *
 * A Thread which listens for incoming TCP requests from clients and handles them accordingly
 *
 */
public class ListenerThread extends Thread {
    final Application app;

    public ListenerThread(final Application app){
        this.app = app;
    }

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
                ServerSocket srv = new ServerSocket(MyActivity.serverPort);
                Log.d("ListenerThread run()", "Check 5 Opening socket");
                // Wait for a client request to come in
                Socket sock = srv.accept();
                Log.d("ListenerThread run()", "Check 6 Creating output stream");
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                Log.d("ListenerThread run()", "Check 7 Creating input stream");
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                // Wait for the clients request message
                while (!in.ready()) {
                    //wait
                }
                Log.d("ListenerThread run()", "Check 8 Reading message");
                // Read the clients request message
                msg = in.readLine();
                Log.d("ListenerThread run()", "Check 9 Parsing message");
                data = msg.split("!!");
                // Check if the request is tagged with "Order"
                Log.d("ListenerThread run()", "Check 9b Message Parsed");
                if (data[2].equals("ORDER")){
                    // Accept the order
                    acceptOrder(data);
                    out.println("Order accepted");
                    sendToast("Incoming order from Table" + data[0]);
                }
                else if (data[2].equals("INIT")){
                    // Sync all clients with the new ID/IP lists
                    String clientAddress = sock.getInetAddress().toString();
                    String cmd = "";
                    // First check that the table is not out of bounds
                    if (Integer.parseInt(data[0])<1 || Integer.parseInt(data[0])>10){
                        // Send an error message to the client
                        cmd = "6000!!" + Arrays.toString(MyActivity.vClock) +
                                "!!INFO!!ERROR: Table ID must be between 1 and 10 inclusive";
                        out.println(cmd);
                    }
                    // Check if this table ID is already taken
                    else if (!MyActivity.tableIPs[Integer.parseInt(data[0])-1].equals("0")){
                        // Send an error message to the client
                        cmd = "6000!!" + Arrays.toString(MyActivity.vClock) +
                                "!!INFO!!ERROR: Table ID already in use";
                        out.println(cmd);
                    }
                    else {
                        // Send clock and IP list to all clients
                        updateIPList(data, clientAddress);
                        cmd = "6000!!" + Arrays.toString(MyActivity.vClock) +
                                "!!ACK!!" + Arrays.toString(MyActivity.tableIPs);
                        out.println(cmd);
                        sendToast("Table" + data[0] + " has come online");
                        out.close();
                        in.close();
                        sock.close();
                        srv.close();
                        broadcastIPs(Integer.parseInt(data[0]));
                        continue;
                    }
                }
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
        String[] items = data[3].split("#");
        // Loop through the items and update the table orders
        Log.d("acceptOrder", "Check 2");
        for (int i=0; i<items.length; i++) {
            item = items[i].split("=")[0];
            qty = Integer.parseInt(items[i].split("=")[1]);
            MyActivity.updateTableOrder("table" + data[0], item, qty);
        }
        // Update the order of the queue
        Log.d("acceptOrder", "Check 3");
        String[] c = data[1].replaceAll("\\[", "").replaceAll("\\]", "").split(", ");
        Log.d("acceptOrder", "Check 4 " + Arrays.toString(c));
        placeOrderInQueue(c, Integer.parseInt(data[0]) - 1);
        Log.d("acceptOrder", "Check 5");
    }

    public void placeOrderInQueue(String[] clock, int cVector){
        // Method for adding an order to the proper slot in the queue
        Log.d("placeOrderInQueue", "Check 3 relVal: " + cVector);
        int iter = 1;
        Log.d("placeOrderInQueue", "clock: " + Arrays.toString(clock));
        Log.d("Check", "" + Integer.parseInt(clock[cVector]));
        Log.d("Check", "" + MyActivity.vClock[cVector]);
        // The vector clock component of the table the order is coming from will either be less
        // than, greater than, or equal to the matching server vector clock. If it is greater,
        // our order queue is in a good state and we can place this order at the tail of the
        // queue. If it is less than or equal to the server clock, then another order that was
        // placed after this order made it to the server first. To maintain FIFO, we handle below.
        if (Integer.parseInt(clock[cVector]) <= MyActivity.vClock[cVector]){
            Log.d("placeOrderInQueue", "Check 4");
            // Loop through the order queue and find the first order that has jumped this order in
            // line. The vector clock will maintain FIFO this way since any order that is processed
            // before an earlier order will get pushed back appropriately.
            for (int i=1; i<11; i++){
                Log.d("placeOrderInQueue", "Check 5");
                Log.d("placeOrderInQueue", "i: " + i);
                Log.d("placeOrderInQueue", "try: " + MyActivity.queue.get(i).split("#")[1].split(",")[cVector]);
                // Check if this queue order is out of place
                if (Integer.parseInt(clock[cVector]) <=
                        Integer.parseInt(MyActivity.queue.get(i).split("#")[1].split(",")[cVector])){
                    Log.d("placeOrderInQueue", "Check 6");
                    // Store the order in the slot temporarily to push the queue
                    String tmp = MyActivity.queue.get(i);
                    Log.d("placeOrderInQueue", "Check 7");
                    // Push every order from this point on in the queue back one spot
                    shiftOrders(i+1, tmp);
                    Log.d("placeOrderInQueue", "Check 8");
                    // Place the order in the queue at this spot. This will be the correct spot
                    // unless it also has jumped another order in which case this will get handled
                    // when that order is processed and maintain our FIFO.
                    MyActivity.queue.put(i, "table" + (cVector + 1) + "#" + TextUtils.join(",", clock));
                    Log.d("placeOrderInQueue", "Check 9");
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
                    MyActivity.queue.put(i, "table" + (cVector + 1) + "#" + TextUtils.join(",", clock));
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
        int[] newClock = new int[10];
        int[] vc = MyActivity.vClock;
        // Loop through each component vector
        for (int i=0; i<10; i++){
            // Check if the vector clock component is greater than the order clock component
            if (vc[i] > Integer.parseInt(clock[i])){
                // Take the vector clock component
                newClock[i] = vc[i];
            }
            else {
                // Take the order clock component
                newClock[i] = Integer.parseInt(clock[i]);
            }
        }
        // Set the vector clock
        MyActivity.vClock = newClock;
        Log.d("Check", "Vector Clock: " + Arrays.toString(MyActivity.vClock));
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

    public void updateIPList(String[] data, String clientAddress){
        // Method for updating the IP address list and broadcasting it to the current clients
        MyActivity.tableIPs[Integer.parseInt(data[0])-1] = clientAddress.replace("/", "");
    }

    public void broadcastIPs(int id){
        // Method for broadcasting the IP address list to all clients
        String cmd = "6000!!" + Arrays.toString(MyActivity.vClock) +
                "INFO!!" + Arrays.toString(MyActivity.tableIPs);
        // Loop through each index in IP list
        for (int i=0; i<10; i++){
            // Check if this is the new table
            if (i==id){
                // Skip this message as it has already been sent
            }
            // Check to see if there is an IP address for this table index
            else if (!MyActivity.tableIPs[i].equals("0")){
                // Make a TCP call sending the new IP lists
                TCPCall(MyActivity.tableIPs[i], cmd);
            }
        }
    }

    public static void TCPCall(String hostname, String cmd){
        try{
            Log.d("TCPCall", "Check 1");
            // Create a socket to the client
            Socket sock = new Socket(hostname, MyActivity.clientPort);
            Log.d("TCPCall", "Check 2");
            // Create a buffer for incoming messages
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            Log.d("TCPCall", "Check 3");
            // Create a stream for outgoing messages
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            Log.d("TCPCall", "Check 4");
            // Send the message
            out.println(cmd);
            Log.d("TCPCall", "Check 5");
            // Close the connection
            out.close();
            Log.d("TCPCall", "Check 6");
            in.close();
            Log.d("TCPCall", "Check 7");
            sock.close();
            Log.d("TCPCall", "Check 8");
        } catch (UnknownHostException exc) {
            Log.d("Error", exc.toString());
        } catch (SocketException exc) {
            Log.d("Error", exc.toString());
        } catch (IOException exc) {
            Log.d("Error", exc.toString());
        }
    }

    public void sendToast(final String msg){
        // Method for creating a toast on the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(app.getApplicationContext(), msg,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

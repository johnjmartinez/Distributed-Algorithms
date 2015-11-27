package com.example.darosale.distributedorderingsystem;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by darosale on 11/23/2015.
 */
public class TestThread extends Thread {
    public final int port = 6000;
    public final String hostname = "localhost";

    public void TCPCall(String cmd){
        String msg = "";
        try{
            Log.d("TCPCall", "Check 1");
            Socket sock = new Socket(hostname, port);
            Log.d("TCPCall", "Check 2");
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            Log.d("TCPCall", "Check 3");
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            Log.d("TCPCall", "Check 4");
            out.println(cmd);
            Log.d("TCPCall", "Check 5");
            while (!in.ready()); {
                //wait
            }
            Log.d("TCPCall", "Check 6");
            Log.d("TCPCall", in.readLine());
            out.close();
            Log.d("TCPCall", "Check 7");
            in.close();
            Log.d("TCPCall", "Check 8");
            sock.close();
            Log.d("TCPCall", "Check 9");
        } catch (UnknownHostException exc) {
            Log.d("Error", exc.toString());
        } catch (SocketException exc) {
            Log.d("Error", exc.toString());
        } catch (IOException exc) {
            Log.d("Error", exc.toString());
        }
    }


    public void run() {
        Log.d("Info", "TestThread started");
        int iter = 1;
        String cmd = "";
        try {
            while (true) {
                Log.d("run", "Check 1");
                Log.d("Check", "ID: " + iter);
                Log.d("Info", "Sleeping for 20 seconds");
                sleep(20000);
                cmd = "" + iter;
                Log.d("run", "Check 3");
                if (iter==1) {
                    Log.d("run", "Check 3a");
                    cmd += "!![1, 0, 0, 0, 0, 0, 0, 0, 0, 0]!!ORDER!!Chicken & Waffles=1";
                }
                else if (iter==2) {
                    Log.d("run", "Check 3b");
                    cmd += "!![1, 1, 1, 0, 0, 0, 0, 0, 0, 0]!!ORDER!!Steak & Potatoes=2";
                }
                else if (iter==3) {
                    Log.d("run", "Check 3c");
                    cmd += "!![1, 0, 1, 0, 0, 0, 0, 0, 0, 0]!!ORDER!!Burger & Fries=1#Steak & Potatoes=2";
                }
                Log.d("run", "Check 4");
                Log.d("Info", "TCP call");
                TCPCall(cmd);
                Log.d("run", "Check 5");
                Log.d("Check", "Increase iter: " + iter);
                iter++;
                Log.d("run", "Check 6");
                cmd = "";
                if (iter>3){
                    Log.d("run", "Check 7");
                    break;
                }
                Log.d("run", "Check 8");
            }
        } catch (Exception exc) {
            System.out.println("There was an exception: " + exc);
        }
    }
}

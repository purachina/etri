package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;

import core.BlockChain;
import core.Transaction;

public class SimulateManu extends Thread {
    private int bcid;
    public SimulateManu() {
        bcid = 0;
    }
    public SimulateManu(int newbcid) {
        this.bcid = newbcid;
    }
    public void run() {
        System.out.println("running sim manu");
        if (bcid == 0) {
            Communicate.addNode("0", Communicate.myip);
            BlockChain.setWorkspace("0");
        }
        else {
            BlockChain.setWorkspace(Integer.toString(bcid));
        }
        ServerSocket server_sock = null;
        try {
            server_sock = new ServerSocket(12345);
            server_sock.setSoTimeout(1000);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (true) {
            synchronized(BlockChain.blockchain) {
                if (UserControl.closechk) break;
                Socket sock;
                String txc = "";
                try {
                    sock = server_sock.accept();
                    System.out.println(sock.getInetAddress().getHostAddress());
                    sock.setSoTimeout(10000);
                    BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    txc = br.readLine();
                    br.close();
                    sock.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
                if (!txc.equals("")) {
                    Transaction tx = new Transaction(txc);
                    BlockChain.addTX(tx);
                }
                if (txc.contains("end")) {
                    BlockChain.mine(Integer.toString(bcid));
                    bcid++;
                    BlockChain.setWorkspace(Integer.toString(bcid));
                    Communicate.addNode(Integer.toString(bcid), Communicate.myip);
                }
            }
        }
    }
}

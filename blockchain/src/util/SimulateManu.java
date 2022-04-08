package util;

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
        if (bcid == 0) {
            Communicate.addNode("0", Communicate.myip);
            BlockChain.setWorkspace("0");
        }
        else {
            BlockChain.setWorkspace(Integer.toString(bcid));
        }
        while (true) {
            synchronized(BlockChain.blockchain) {
                if (UserControl.closechk) break;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                LocalDateTime now = LocalDateTime.now();
                Random rand = new Random();
                int r = rand.nextInt(30);
                Transaction tx = new Transaction("Time: " + now.toString() + " Measure: " + r);
                BlockChain.addTX(tx);
                if (r >= 20) {
                    BlockChain.mine(Integer.toString(bcid));
                    bcid++;
                    BlockChain.setWorkspace(Integer.toString(bcid));
                    Communicate.addNode(Integer.toString(bcid), Communicate.myip);
                }
            }
        }
    }
}

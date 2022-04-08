package util;

import java.time.LocalDateTime;
import java.util.Random;

import core.BlockChain;
import core.Transaction;

public class SimulateServ extends Thread {
    public void run() {
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
                    BlockChain.mine(Integer.toString(BlockChain.getBCID()));
                    return;
                }
            }
        }
    }
}

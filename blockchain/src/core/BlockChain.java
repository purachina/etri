package core;

import java.util.*;

import util.Communicate;
import util.Init;
import util.SerialIO;
import util.UserControl;
public class BlockChain extends Thread {
    private static Block cblock;
    private static ArrayList<Block> blockchain = new ArrayList<Block>();
    public BlockChain() {
        Init.init();
    }
    public static Block getCurrentBlock() {
        return new Block(cblock);
    }
    public static ArrayList<Block> getBlockChain() {
        ArrayList<Block> ret = new ArrayList<Block>();
        synchronized (blockchain) {
            for (int i = 0; i < blockchain.size(); i++) {
                ret.add(new Block(blockchain.get(i)));
            }
        }
        return ret;
    }
    public static int printBlockChain() {
        for (int i = 0; i < blockchain.size(); i++) {
            blockchain.get(i).printBlock();
        }
        return 0;
    }
    public static int setBlockchain(ArrayList<Block> newbc) {
        synchronized(blockchain) {
            ArrayList<Block> tmpbc = new ArrayList<Block>();
            Block tmpblock;
            for (int i = 0; i < newbc.size(); i++) {
                tmpblock = new Block(newbc.get(i));
                if (!tmpblock.getAvailable()) {
                    return 1;
                }
                else tmpbc.add(tmpblock);
            }
            blockchain = tmpbc;
        }
        return 0;
    }
    public static int addTX(Transaction tx) {
        synchronized(cblock) {
            cblock.addTX(new Transaction(tx));
        }
        return 0;
    }
    public static class MiningThread extends Thread {
        public void run() {
            while(true) {
                Transaction coinbase_tx = new Transaction();
                Block newblock = cblock.mine(coinbase_tx);
                if (newblock != null) {
                    synchronized (blockchain) {
                        blockchain.add(newblock);
                        cblock = newblock;
                    }
                }
                if (UserControl.closechk == true) return;
            }
        }
    }
    public void run() {
        if (blockchain == null) blockchain = new ArrayList<Block>();
        if (blockchain.size() == 0) blockchain.add(new Block());
        cblock = blockchain.get(blockchain.size() - 1);
        while(true) {
            Transaction coinbase_tx = new Transaction();
            Block newblock = cblock.mine(coinbase_tx);
            if (newblock != null) {
                synchronized (blockchain) {
                    blockchain.add(newblock);
                    cblock = newblock;
                }
            }
            if (UserControl.closechk == true) return;
        }
    }
}

package core;

import java.util.*;

import util.Communicate;
import util.Init;
import util.SerialIO;
import util.UserControl;
import util.Network.DistributeBlockThread;
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
    private static boolean newBlockCheck(Block cblock, Block newblock) {
        if (cblock.getNumberofTX() != newblock.getNumberofTX()) return false;
        if (cblock.getDifficulty().equals(newblock.getDifficulty())) {
            if (newblock.getBlockHash().substring(0, newblock.getDifficulty().length()).compareTo(newblock.getDifficulty()) <= 0) {
                if (new Block(newblock).getAvailable()) {
                    return true;
                }
            }
        }
        return false;
    }
    public static int acceptBlock(Object recv) {
        int ret = 1;
        synchronized(blockchain) {
            if (recv instanceof ArrayList) {
                if (((ArrayList)recv).get(0) instanceof Block && ((ArrayList)recv).get(1) instanceof Block) {
                    ArrayList<Block> newblocks = (ArrayList<Block>) recv;
                    if (newblocks.get(0).getPreBlockHash().equals(cblock.getPreBlockHash()) && cblock.chkRecvBlock(newblocks.get(0))) {
                        blockchain.add(newblocks.get(0));
                        blockchain.add(newblocks.get(1));
                        cblock = blockchain.get(blockchain.size() - 1);
                        ret = 0;
                    }
                    else {
                        System.out.println("##########################");
                        newblocks.get(0).printBlock();
                    }
                }
            }
        }
        return ret;
    }/*
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
    */
    public void run() {
        if (blockchain == null) blockchain = new ArrayList<Block>();
        if (blockchain.size() == 0) blockchain.add(new Block());
        cblock = blockchain.get(blockchain.size() - 1);
        while(true) {
            Transaction coinbase_tx = new Transaction(Communicate.myip, Communicate.myip, "123");
            while(true) {
                cblock = blockchain.get(blockchain.size() - 1);
                cblock.addnonce();
                synchronized (cblock) {
                    if (UserControl.closechk == true) return;
                    if (cblock.getBlockHash().substring(0, cblock.getDifficulty().length()).compareTo(cblock.getDifficulty()) <= 0) {
                        System.out.println("block mined!!!");
                        cblock.printBlock();
                        Block newblock = new Block(cblock, cblock.getBlockID() + 1, 0, coinbase_tx, "00000");
                        
                        for (int i = 0; i < Communicate.getNodeList().size(); i++) {
                            if (!Communicate.getNodeList().get(i).equals(Communicate.myip)) {
                                System.out.println("distributing this block to " + Communicate.getNodeList().get(i));
                                DistributeBlockThread dbt = new DistributeBlockThread(Communicate.getNodeList().get(i), new Block(cblock), new Block(newblock));
                                dbt.start();
                            }
                        }
                        blockchain.add(newblock);
                    }
                }
            }
        }
    }
}

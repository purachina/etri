package core;

import java.util.*;

import util.Communicate;
import util.Init;
import util.SerialIO;
import util.UserControl;
import util.Network.DistributeBlockThread;
public class BlockChain {
    private static Block cblock;
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, ArrayList<Block>> blockchaindict = new HashMap<String, ArrayList<Block>>();
    public BlockChain() {
        while (true) {
            int chk = Init.init();
            if (chk == 0) break;
        }
    }
    public static void setWorkspace(String bcid) {
        if (blockchaindict.containsKey(bcid)) {
            blockchain = blockchaindict.get(bcid);
            cblock = blockchain.get(blockchain.size() - 1);
        }
        else {
            blockchain = new ArrayList<Block>();
            cblock = new Block();
            blockchain.add(cblock);
            if (blockchaindict == null) blockchaindict = new HashMap<String, ArrayList<Block>>();
            blockchaindict.put(bcid, blockchain);
        }
    }
    public static Block getCurrentBlock(String bcid) {
        Block ret = null;
        synchronized(blockchaindict) {
            ret = blockchaindict.get(bcid).get(blockchaindict.get(bcid).size() - 1);
        }
        return ret;
    }
    public static ArrayList<Block> getBlockChain(String bcid) {
        ArrayList<Block> ret = null;
        synchronized (blockchaindict) {
            ret = blockchaindict.get(bcid);
        }
        return ret;
    }
    public static int printBlockChain(String bcid) {
        synchronized(blockchaindict) {
            if (!blockchaindict.containsKey(bcid)) return 0;
            System.out.println(bcid + "th blockchain");
            for (int i = 0; i < blockchaindict.get(bcid).size(); i++) {
                blockchaindict.get(bcid).get(i).printBlock();
            }
        }
        return 0;
    }
    public static int getBCID() {
        int ret;
        synchronized (blockchaindict) {
            if (blockchaindict.size() > 0) {
                String[] tmparr = blockchaindict.keySet().toArray(new String [blockchaindict.size()]);
                ret = Integer.parseInt(tmparr[blockchaindict.size() - 1]);
            }
            else ret = 0;
        }
        return ret;
    }
    public static HashMap<String, ArrayList<Block>> getBCDict() {
        HashMap<String, ArrayList<Block>> ret = null;
        synchronized(blockchaindict) {
            ret = new HashMap<String, ArrayList<Block>>();
            String [] bcids = blockchaindict.keySet().toArray(new String[blockchaindict.size()]);
            for (int i = 0; i < bcids.length; i++) {
                ArrayList<Block> tmpbc = new ArrayList<Block>();
                for (int j = 0; j < blockchaindict.get(bcids[i]).size(); j++) {
                    tmpbc.add(new Block(blockchaindict.get(bcids[i]).get(j)));
                }
                ret.put(bcids[i], tmpbc);
            }
        }
        return ret;
    }
    public static int loadBlockChainDict(HashMap<String, ArrayList<Block>> newbcdict) {
        synchronized(blockchaindict) {
            String [] bcids = newbcdict.keySet().toArray(new String[newbcdict.size()]);
            for (int i = 0; i < bcids.length; i++) {
                acceptBlockChain(bcids[i], newbcdict.get(bcids[i]));
            }
        }
        return 0;
    }
    public static int acceptBlockChain(String bcid, ArrayList<Block> newbc) {
        synchronized(blockchaindict) {
            Block tmpblock;
            ArrayList<Block> tmpbc = new ArrayList<Block>();
            for (int i = 0; i < newbc.size(); i++) {
                tmpblock = new Block(newbc.get(i));
                if (!tmpblock.getAvailable()) {
                    return 1;
                }
                else tmpbc.add(tmpblock);
            }
            blockchaindict.put(bcid, tmpbc);
            setWorkspace(bcid);
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
    public static int acceptBlock(String bcid, Object recv) {
        int ret = 1;
        synchronized(blockchaindict) {
            if (recv instanceof ArrayList) {
                if (((ArrayList)recv).get(0) instanceof Block && ((ArrayList)recv).get(1) instanceof Block) {
                    ArrayList<Block> newblocks = (ArrayList<Block>) recv;
                    if (newblocks.get(0).getPreBlockHash().equals(cblock.getPreBlockHash()) && blockchaindict.get(bcid).get(blockchaindict.get(bcid).size() - 1).chkRecvBlock(newblocks.get(0))) {
                        blockchaindict.get(bcid).remove(blockchain.size() - 1);
                        blockchaindict.get(bcid).add(newblocks.get(0));
                        blockchaindict.get(bcid).add(newblocks.get(1));
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
    public static int mine(String bcid) {
        Transaction coinbase_tx = new Transaction(Communicate.myip);
        while(true) {
            setWorkspace(bcid);
            synchronized (cblock) {
                cblock.addnonce();
                if (UserControl.closechk == true) return 0;
                if (cblock.getBlockHash().substring(0, cblock.getDifficulty().length()).compareTo(cblock.getDifficulty()) <= 0) {
                    Block newblock = new Block(cblock, cblock.getBlockID() + 1, 0, coinbase_tx, "0000");
                    ArrayList<String> network = Communicate.getNodeList(bcid);
                    for (int i = 0; i < network.size(); i++) {
                        if (!network.get(i).equals(Communicate.myip)) {
                            System.out.println("distributing this block to " + network.get(i));
                            DistributeBlockThread dbt = new DistributeBlockThread(network.get(i), bcid, new Block(cblock), new Block(newblock));
                            dbt.start();
                        }
                    }
                    blockchain.add(newblock);
                    return 0;
                }
            }
        }
    }
    /*
    public void run() {
        if (blockchain == null) blockchain = new ArrayList<Block>();
        if (blockchain.size() == 0) blockchain.add(new Block());
        cblock = blockchain.get(blockchain.size() - 1);
        while(true) {
            Transaction coinbase_tx = new Transaction(Communicate.myip);
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
    */
}

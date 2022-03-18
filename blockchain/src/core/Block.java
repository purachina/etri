package core;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import util.Hashing;
public class Block {
    private String pre_block_hash, difficulty, merkle_root;
    private MerkleTree merkle_tree;
    private int block_id, nonce, timestamp;
    private Lock lk;

    public Lock getLock() {return lk;}
    public Block() {
        this.pre_block_hash = null;
        this.block_id = 0;
        this.nonce = 0;
        this.merkle_tree = new MerkleTree();
        this.merkle_root = merkle_tree.getMerkleRoot();
        this.difficulty = "0";
        this.timestamp = 0;
        this.lk = new ReentrantLock();
    }
    public Block(String pre_block_hash, int block_id,
    int nonce, Transaction tx, String difficulty) {
        this.pre_block_hash = pre_block_hash;
        this.block_id = block_id;
        this.nonce = nonce;
        this.merkle_tree = new MerkleTree(tx);
        this.merkle_root = merkle_tree.getMerkleRoot();
        this.difficulty = difficulty;
        this.timestamp = 0;
        this.lk = new ReentrantLock();
    }

    public String getPreBlockHash() {return this.pre_block_hash;}
    public void setpre_block_hash(String pre_block_hash) {
        this.pre_block_hash = pre_block_hash;
    }
    public int getBlockID() {return block_id;}
    public void setBlockID(int block_id) {this.block_id = block_id;}
    public int getNonce() {return nonce;}
    public void setNonce(int nonce) {this.nonce = nonce;}
    public String getDifficulty() {return difficulty;}
    public ArrayList<Transaction> getTXList() {
        return this.merkle_tree.getTXList();
    }
    public int getTimestamp() {return timestamp;}
    

    public synchronized void addTX(Transaction new_tx) {
        this.merkle_tree.addTX(new_tx);
        this.setNonce(0);
        this.timestamp++;
    }

    public String getBlockHash() {
        return Hashing.getHash(pre_block_hash + nonce + merkle_root + timestamp);
    }
    public void getBlockInfo(){
        ArrayList<Transaction> tx_list = merkle_tree.getTXList();
        System.out.println("=========================================================");
        System.out.println("pre_block_hash: " + getPreBlockHash());
        System.out.println("block ID: " + getBlockID());
        System.out.println("block nonce: " + getNonce());
        System.out.println("difficulty: " + getDifficulty());
        System.out.println("number of TXs: " + merkle_tree.getTXList().size());
        System.out.println("----------------TRANSACTIONS----------------");
        for (int i = 0; i < tx_list.size(); i++) {
            System.out.println(tx_list.get(i).getInfo());
        }
        System.out.println("----------------TRANSACTIONS----------------");
        System.out.println("block hash: " + getBlockHash());
        System.out.println("=========================================================");
    }

    public Block mine() {
        long time = System.currentTimeMillis();
        Transaction coinbase_tx;
        while (true) {
            if(
                getBlockHash().substring(
                    0, difficulty.length()
                ).compareTo(difficulty) <= 0) {
                //System.out.println(block_id + "th block mined!!!");
                coinbase_tx = new Transaction("minor", "minor", 12.5);
                break;
            }
            nonce++;
        }
        time = System.currentTimeMillis() - time;
        if (time < 6000) {
            return new Block
            (this.getBlockHash(),
            this.getBlockID() + 1,
            this.getNonce(),
            coinbase_tx,
            this.getDifficulty() + "0");
        }
        else if (time > 7000) {
            return new Block
            (this.getBlockHash(),
            this.getBlockID() + 1,
            this.getNonce(),
            coinbase_tx,
            this.getDifficulty().substring(1));
        }
        else {
            return new Block
            (this.getBlockHash(),
            this.getBlockID() + 1,
            this.getNonce(),
            coinbase_tx,
            this.getDifficulty());
        }
    }
}

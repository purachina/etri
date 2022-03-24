package core;
import java.io.Serializable;
import java.util.ArrayList;

import core.Atom.HashString;
import util.Hashing;

public class Block implements Serializable {
    private HashString pre_block_hash, blockhash, merkleroot;
    private String difficulty;
    private MerkleTree merkletree;
    private int blockid, nonce, timestamp;

    public Block() {
        this.pre_block_hash = new HashString();
        this.blockid = 0;
        this.nonce = 0;
        this.merkletree = new MerkleTree();
        this.merkleroot = merkletree.merklelist.get(merkletree.merklelist.size() - 1);
        this.difficulty = "0";
        this.timestamp = 0;
    }
    public Block(Block preblock, int block_id,
    int nonce, Transaction tx, String difficulty) {
        setPreBlockHash(preblock);
        setBlockID(block_id);
        setNonce(nonce);
        this.merkletree = new MerkleTree(tx);
        this.merkleroot = merkletree.merklelist.get(merkletree.merklelist.size() - 1);
        this.difficulty = difficulty;
        this.timestamp = 0;
        this.blockhash = this.getBlockHash();
    }

    private HashString getPreBlockHash() {return this.pre_block_hash;}
    private int setPreBlockHash(Block preblock) {
        this.pre_block_hash = preblock.blockhash;
        return 0;
    }
    public int getBlockID() {return blockid;}
    private int setBlockID(int newbid) {
        this.blockid = newbid;
        return 0;
    }
    public int getNonce() {
        int tmpn;
        synchronized(this) {tmpn = nonce;}
        return tmpn;
    }
    private int setNonce(int nonce) {
        this.nonce = nonce;
        return 0;
    }
    public String getDifficulty() {return difficulty;}
    private int setDifficulty(String difficulty) {
        this.difficulty = new String(difficulty);
        return 0;
    }
    private ArrayList<HashString> getMerkleTree() {
        ArrayList<HashString> tmpmt;
        synchronized(this) {tmpmt = this.merkletree.merklelist;}
        return tmpmt;
    }
    private int setMerkleTree(MerkleTree newmt) {
        this.merkletree = newmt;
        return 0;
    }
    private HashString getMerkleRoot() {
        HashString tmpmr;
        synchronized(this) {tmpmr = merkleroot;}
        return tmpmr;
    }
    private int setMerkleRoot() {
        this.merkle_tree.getMerkleRoot();
    }
    public int getTimestamp() {
        int tmpts;
        synchronized(this) {tmpts = timestamp;}
        return tmpts;
    }
    public Block getBlock() {
        Block tmpb;
        synchronized(this) {tmpb = new Block(this);}
        return tmpb;
    }
    public HashString getBlockHash() {
        return new HashString(Hashing.getHash(this));
    }
    public Block getBlockCopy() {
        
    }
    public void printBlock(){
        ArrayList<Transaction> tx_list = merkle_tree.getTXList();
        System.out.println("=========================================================");
        System.out.print("pre_block_hash: " + getPreBlockHash().getBHCopy());
        System.out.println("block ID: " + getBlockID());
        System.out.println("block nonce: " + getNonce());
        System.out.println("difficulty: " + getDifficulty());
        System.out.println("number of TXs: " + merkle_tree.getTXList().size());
        System.out.println("merkle root: " + getMerkleRoot());
        System.out.println("----------------TRANSACTIONS----------------");
        for (int i = 0; i < tx_list.size(); i++) {
            System.out.println(tx_list.get(i).getInfo());
        }
        System.out.println("----------------TRANSACTIONS----------------");
        System.out.println("block hash: " + getBlockHash());
        System.out.println("=========================================================");
    }
    
    public void addTX(Transaction new_tx) {
        synchronized(this) {
            this.merkle_tree.addTX(new_tx);
            this.merkle_root = this.merkle_tree.getMerkleRoot();
            this.setNonce(0);
            this.timestamp++;
        }
    }

    public Block mine() {
        long time = System.currentTimeMillis();
        Transaction coinbase_tx;
        while (true) {
            synchronized (this) {
                if (getBlockHash().bh.substring(0, difficulty.length()).compareTo(difficulty) <= 0) {
                    coinbase_tx = new Transaction("minor", "minor", 12.5);
                    time = System.currentTimeMillis() - time;
                    if (time < 6000) {
                        return new Block(
                                this,
                                this.getBlockID() + 1,
                                this.getNonce(),
                                coinbase_tx,
                                this.getDifficulty() + "0");
                    } else if (time > 7000) {
                        return new Block(
                                this,
                                this.getBlockID() + 1,
                                this.getNonce(),
                                coinbase_tx,
                                this.getDifficulty().substring(1));
                    } else {
                        return new Block(
                                this,
                                this.getBlockID() + 1,
                                this.getNonce(),
                                coinbase_tx,
                                this.getDifficulty());
                    }
                }
                nonce++;
            }
        }
    }
}

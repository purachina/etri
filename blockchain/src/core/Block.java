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
    public Block(Block preblock, int blockid,
    int nonce, Transaction tx, String difficulty) {
        this.pre_block_hash = preblock.blockhash;
        this.blockid = blockid;
        this.nonce = nonce;
        this.merkletree = new MerkleTree(tx);
        this.merkleroot = merkletree.merklelist.get(merkletree.merklelist.size() - 1);
        this.difficulty = difficulty;
        this.timestamp = 0;
        this.blockhash = this.makeBlockHash();
    }
    public String getPreBlockHash() {
        return this.pre_block_hash.getHashCopy();
    }
    public int getBlockID() {return this.blockid;}
    public int getNonce() {
        int tmpn;
        synchronized(this) {tmpn = nonce;}
        return tmpn;
    }
    public ArrayList<Transaction> getTXList() {
        ArrayList<Transaction> tmparr;
        synchronized(this.merkletree) {tmparr = merkletree.getTXListCopy();}
        return tmparr;
    }
    public ArrayList<MerkleTree> getMerkleTree() {
        this.merkletree.g
    }
    public String getMerkleRoot() {
        String tmps;
        synchronized(this.merkletree) {tmps = merkletree.getMerkleRootCopy().getHashCopy();}
        return tmps;
    }
    public String getDifficulty() {return new String(this.difficulty);}
    public int getTimeStamp() {return this.timestamp;}
    public String getBlockHash() {
        String tmps;
        synchronized(this) {tmps = new String(this.blockhash.getHashCopy());}
        return tmps;
    }



    private HashString getPreBlockHashOrg() {return this.pre_block_hash;}
    private ArrayList<HashString> getMerkleTreeOrg() {
        ArrayList<HashString> tmpmt;
        synchronized(this) {tmpmt = this.merkletree.merklelist;}
        return tmpmt;
    }
    private HashString getMerkleRootOrg() {
        HashString tmpmr;
        synchronized(this) {tmpmr = merkleroot;}
        return tmpmr;
    }
    private int getTimestamp() {
        int tmpts;
        synchronized(this) {tmpts = timestamp;}
        return tmpts;
    }
    private Block getBlockOrg() {
        Block tmpb;
        synchronized(this) {tmpb = this;}
        return tmpb;
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

    private HashString makeBlockHash() {
        HashString hs = null;
        synchronized(this) {hs = new HashString(Hashing.getHash(this));}
        return hs;
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

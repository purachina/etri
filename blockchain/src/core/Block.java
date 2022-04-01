package core;
import java.io.Serializable;
import java.util.ArrayList;

import util.Communicate;
import util.Consensus;
import util.Hashing;
import util.Network;
import util.UserControl;
import util.Network.DistributeBlockThread;

public class Block implements Serializable {
    private String pre_block_hash, blockhash, merkleroot, difficulty;
    private MerkleTree merkletree;
    private int blockid, nonce, timestamp;
    private boolean available;
    public Block() {
        this.pre_block_hash = null;
        this.blockid = 0;
        this.nonce = 0;
        this.merkletree = new MerkleTree(new Transaction());
        this.difficulty = "0";
        this.timestamp = 0;
        this.available = true;
        this.refresh();
    }
    public Block(Block preblock, int newblockid,
    int newnonce, Transaction newtx, String newdifficulty) {
        this.pre_block_hash = new String(preblock.getBlockHash());
        this.blockid = newblockid;
        this.nonce = newnonce;
        this.merkletree = new MerkleTree(newtx);
        this.difficulty = new String(newdifficulty);
        this.timestamp = 0;
        this.available = true;
        this.refresh();
    }
    public Block(Block newblock) {
        synchronized(newblock) {
            this.available = true;
            this.pre_block_hash = newblock.getPreBlockHash();
            this.blockid = newblock.getBlockID();
            this.nonce = newblock.getNonce();
            this.setMerkleTree(newblock.merkletree);
            this.difficulty = new String(newblock.getDifficulty());
            this.timestamp = newblock.timestamp;
            this.refresh();
            if (!this.getBlockHash().equals(newblock.getBlockHash()) && this.available != false) {
                this.available = false;
            }
            else {this.available = true;}
        }
    }
    public String getPreBlockHash() {
        if (this.pre_block_hash != null) return new String(this.pre_block_hash);
        return "null";
    }
    public int getBlockID() {return this.blockid;}
    public int getNonce() {
        int ret;
        synchronized(this) {ret = this.nonce;}
        return ret;
    }
    public ArrayList<Transaction> getTXList() {
        ArrayList<Transaction> ret;
        synchronized(this.merkletree) {ret = this.merkletree.getTXList();}
        return ret;
    }
    public MerkleTree getMerkleTree() {
        MerkleTree ret;
        synchronized(this.merkletree) {
            this.refresh();
            ret = new MerkleTree(this.merkletree);
        }
        return ret;
    }
    public String getMerkleRoot() {
        String ret;
        synchronized(this.merkletree) {
            this.refresh();
            ret = new String(this.merkletree.getMerkleRoot());
        }
        return ret;
    }
    public String getDifficulty() {return new String(this.difficulty);}
    public int getTimeStamp() {return this.timestamp;}
    public String getBlockHash() {
        String ret;
        synchronized(this) {
            this.refresh();
            ret = new String(this.blockhash);
        }
        return ret;
    }
    public boolean getAvailable() {return this.available;}
    private int setMerkleRoot() {
        synchronized(this) {
            this.merkletree.makeMerkle();
            this.merkleroot = this.merkletree.getMerkleRoot();
        }
        return 0;
    }
    private int setMerkleTree(MerkleTree newmt) {
        synchronized(this) {
            synchronized(newmt) {
                this.merkletree = new MerkleTree(newmt);
                this.refresh();
                if (!this.getMerkleRoot().equals(newmt.getMerkleRoot())) {
                    System.out.println("There's a problem with merkletree");
                    this.available = false;
                    return 1;
                }
            }
        }
        return 0;
    }
    public int refresh() {
        synchronized(this) {
            this.setMerkleRoot();
            this.blockhash = Hashing.makeHash(this.pre_block_hash + this.nonce + this.timestamp + this.merkleroot);
        }
        return 0;
    }
    public int getTimestamp() {
        int ret;
        synchronized(this) {ret = timestamp;}
        return ret;
    }
    public int getNumberofTX() {
        int ret;
        synchronized(this) {
            ret = merkletree.getTXList().size();
        }
        return ret;
    }
    public int printBlock() {
        synchronized (this) {
            this.refresh();
            ArrayList<Transaction> tmptxlist = this.merkletree.getTXList();
            System.out.println("Pre_block_hash = " + this.getPreBlockHash());
            System.out.println("Block ID = " + this.getBlockID());
            System.out.println("Nonce = " + this.getNonce());
            System.out.println("Difficulty = " + this.getDifficulty());
            System.out.println("Merkleroot = " + this.getMerkleRoot());
            System.out.println("Number of TX = " + tmptxlist.size());;
            for (int i = 0; i < tmptxlist.size(); i++) {
                tmptxlist.get(i).printTX();
            }
            System.out.println("Block hash = " + this.getBlockHash());
        }
        System.out.println("============================================");
        return 0;
    }
    public String getBlockInfo(){
        String ret;
        synchronized (this) {
            this.refresh();
            ArrayList<Transaction> tmptxlist = this.merkletree.getTXList();
            ret = "Pre_block_hash = " + this.getPreBlockHash() + "\n";
            ret += "Block ID = " + this.getBlockID() + "\n";
            ret += "Nonce = " + this.getNonce() + "\n";
            ret += "Difficulty = " + this.getDifficulty() + "\n";
            ret += "Merkleroot = " + this.getMerkleRoot() + "\n";
            ret += "Number of TX = " + tmptxlist.size() + "\n";
            ret += "\n";
            for (int i = 0; i < tmptxlist.size(); i++) {
                ret += tmptxlist.get(i).getInfo() + "\n";
            }
            ret += "Block hash = " + this.getBlockHash();
        }
        return ret;
    }

    public boolean equals(Block target) {
        return target.getBlockHash().equals(this.getBlockHash());
    }

    public boolean chkRecvBlock(Block recv) {
        recv.refresh();
        if (!recv.getDifficulty().equals(this.getDifficulty())) return false;
        if (recv.getBlockHash().substring(0, this.getDifficulty().length()).compareTo(this.getDifficulty()) <= 0) return true;
        return false;
    }

    public void addTX(Transaction newtx) {
        synchronized(this) {
            this.merkletree.addTX(newtx);
            this.nonce = 0;
            this.timestamp++;
            this.refresh();
        }
    }

    public Block mine(Transaction coinbase_tx) {
        long time = System.currentTimeMillis();
        while (true) {
            synchronized (this) {
                if (UserControl.closechk == true) return null;
                if (this.getBlockHash().substring(0, difficulty.length()).compareTo(difficulty) <= 0) {
                    System.out.println("block mined!!!");
                    time = System.currentTimeMillis() - time;
                    Block ret;
                    if (time < 6000) {
                        ret = new Block(
                                this,
                                this.getBlockID() + 1,
                                this.getNonce(),
                                coinbase_tx,
                                this.getDifficulty() + "0");
                    }
                    else if (time > 7000) {
                        ret = new Block(
                                this,
                                this.getBlockID() + 1,
                                this.getNonce(),
                                coinbase_tx,
                                this.getDifficulty().substring(1));
                    }
                    else {
                        ret = new Block(
                                this,
                                this.getBlockID() + 1,
                                this.getNonce(),
                                coinbase_tx,
                                this.getDifficulty());
                    }
                    Consensus.initPoW();
                    for (int i = 0; i < Communicate.getNodeList().size(); i++) {
                        if (!Communicate.getNodeList().get(i).equals(Communicate.myip)) {
                            System.out.println("distributing this block to " + Communicate.getNodeList().get(i));
                            DistributeBlockThread dbt = new DistributeBlockThread(Communicate.getNodeList().get(i),
                                    new Block(this), new Block(ret));
                            dbt.start();
                        }
                    }
                    while (true) {
                        if (Consensus.chkPoW()) return ret;
                    }
                }
                nonce++;
            }
        }
    }
}

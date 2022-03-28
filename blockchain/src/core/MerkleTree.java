package core;
import java.io.Serializable;
import java.util.ArrayList;

import util.Hashing;

public class MerkleTree implements Serializable {
    private ArrayList<Transaction> txlist;
    protected ArrayList<String> merklelist;
    public MerkleTree(Transaction coinbase_tx) {
        this.initMerkleTree(coinbase_tx);
    }
    public MerkleTree(MerkleTree target) {
        this.setTXList(target.txlist);
        this.makeMerkle();
        if (!this.getMerkleList().equals(target.getMerkleList())) {
            System.out.println("Merkletree does not matched!");
        }
    }

    
    public ArrayList<Transaction> getTXList() {
        ArrayList<Transaction> ret;
        ret = new ArrayList<Transaction>();
        synchronized(this) {
            for(int i = 0; i < txlist.size(); i++) {
                ret.add(txlist.get(i).getTX());
            }
        }
        return ret;
    }
    private int setTXList(ArrayList<Transaction> newtxlist) {
        this.txlist = new ArrayList<Transaction>();
        synchronized(newtxlist) {
            for(int i = 0; i < newtxlist.size(); i++) {
                this.txlist.add(newtxlist.get(i));
            }
        }
        return 0;
    }
    public String getMerkleRoot() {
        this.makeMerkle();
        String ret = null;
        synchronized(merklelist) {
            ret = new String(merklelist.get(merklelist.size() - 1));
        }
        return ret;
    }
    public ArrayList<String> getMerkleList() {
        ArrayList<String> ret = new ArrayList<String>();
        synchronized(this) {
            for(int i = 0; i < this.merklelist.size(); i++) {
                ret.add(merklelist.get(i));
            }
        }
        return ret;
    }

    public int addTX(Transaction newtx) {
        synchronized(this) {
            txlist.add(newtx);
            makeMerkle();
        }
        return 0;
    }
    private int initMerkleTree(Transaction coinbase_tx) {
        synchronized(this) {
            txlist = new ArrayList<Transaction>();
            txlist.add(coinbase_tx);
            this.makeMerkle();
        }
        return 0;
    }

    public int makeMerkle() {
        synchronized(this) {
            String hash1, hash2;
            merklelist = new ArrayList<String>();
            for (int i = 0; i < txlist.size(); i += 2) {
                if (i + 1 == txlist.size()) {
                    hash1 = new String(Hashing.makeHash(txlist.get(i).getHash()));
                    this.merklelist.add(hash1);
                }
                else {
                    hash1 = new String(Hashing.makeHash(txlist.get(i).getHash()));
                    hash2 = new String(Hashing.makeHash(txlist.get(i + 1).getHash()));
                    this.merklelist.add(new String(Hashing.makeHash(hash1 + hash2)));
                }
            }
            int piv = 0;
            while (true) {
                int cSize = this.merklelist.size();
                if (cSize == piv + 1) break;
                for (; piv < cSize; piv += 2) {
                    if (piv + 1 == this.merklelist.size()) {
                        hash1 = new String(Hashing.makeHash(this.merklelist.get(piv)));
                        this.merklelist.add(hash1);
                        piv--;
                    }
                    else {
                        hash1 = new String(Hashing.makeHash(this.merklelist.get(piv)));
                        hash2 = new String(Hashing.makeHash(this.merklelist.get(piv + 1)));
                        this.merklelist.add(new String(Hashing.makeHash(hash1 + hash2)));
                    }
                }
            }
            return 0;
        }
    }
}

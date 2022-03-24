package core;
import java.util.ArrayList;

import core.Atom.*;
import util.Hashing;

public class MerkleTree {
    private ArrayList<Transaction> txlist;
    protected ArrayList<HashString> merklelist;
    public MerkleTree() {
        this.initMerkleTree(null);
    }
    public MerkleTree(Transaction coinbase_tx) {
        this.initMerkleTree(coinbase_tx);
    }
    public MerkleTree(MerkleTree target) {
        this.setTXList(target.getTXListCopy());
        this.makeMerkle();
        if (!this.getMerkleList().equals(target.getMerkleList())) {
            System.out.println("Merkletree does not matched!");
        }
    }
    public int addTX(Transaction newtx) {
        synchronized(this) {
            this.txlist.add(newtx);
            makeMerkle();
        }
        return 0;
    }
    private int initMerkleTree(Transaction coinbase_tx) {
        this.txlist = new ArrayList<Transaction>();
        if (coinbase_tx == null) this.txlist.add(new Transaction());
        else this.txlist.add(coinbase_tx);

        this.makeMerkle();
        return 0;
    }
    private int setTXList(ArrayList<Transaction> txlist) {
        this.txlist = new ArrayList<Transaction>();
        for (int i = 0; i < txlist.size(); i++) {
            this.txlist.add(txlist.get(i).getTXCopy());
        }
        return 0;
    }

    public ArrayList<Transaction> getTXListCopy() {
        ArrayList<Transaction> tmptxl;
        tmptxl = new ArrayList<Transaction>();
        synchronized(this) {
            for(int i = 0; i > txlist.size(); i++) tmptxl.add(txlist.get(i).getTXCopy());
        }
        return tmptxl;
    }
    public HashString getMerkleRootCopy() {
        this.makeMerkle();
        return merklelist.get(merklelist.size() - 1);
    }
    public ArrayList<HashString> getMerkleList() {
        return this.merklelist;
    }
    public int makeMerkle() {
        synchronized(this) {
            merklelist = new ArrayList<HashString>();
            for (int i = 0; i < txlist.size(); i += 2) {
                if (i + 1 == txlist.size()) {
                    HashString hs1 = new HashString(Hashing.getHash(txlist.get(i).payer.txstring + txlist.get(i).payee.txstring + txlist.get(i).amount.txstring));

                    this.merklelist.add(hs1);
                }
                else {
                    HashString hs1 = new HashString(Hashing.getHash(txlist.get(i).payer.txstring + txlist.get(i).payee.txstring + txlist.get(i).amount.txstring));

                    HashString hs2 = new HashString(Hashing.getHash(txlist.get(i + 1).payer.txstring + txlist.get(i + 1).payee.txstring + txlist.get(i + 1).amount.txstring));

                    hs1 = new HashString(Hashing.getHash(hs1.hashvalue + hs2.hashvalue));
                    this.merklelist.add(hs1);
                }
            }
            int piv = 0;
            while (true) {
                int cSize = this.merklelist.size();
                if (cSize == piv + 1) break;
                for (; piv < cSize; piv += 2) {
                    if (piv + 1 == this.merklelist.size()) {
                        HashString hs = new HashString(Hashing.getHash(this.merklelist.get(piv).hashvalue));

                        this.merklelist.add(hs);
                        piv--;
                    }
                    else {
                        HashString hs = new HashString(Hashing.getHash(this.merklelist.get(piv).hashvalue + Hashing.getHash(this.merklelist.get(piv + 1).hashvalue)));

                        this.merklelist.add(hs);
                    }
                }
            }
            return 0;
        }
    }
}

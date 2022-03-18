package core;
import java.util.ArrayList;
import util.Hashing;

public class MerkleTree {
    private ArrayList<Transaction> tx_list;
    private ArrayList<String> merkle_list;
    public MerkleTree() {
        this.tx_list = new ArrayList<Transaction>();
        this.tx_list.add(new Transaction());
        this.merkle_list = new ArrayList<String> ();
        this.makeMerkle();
    }
    public MerkleTree(Transaction coinbase_tx) {
        this.tx_list = new ArrayList<Transaction>();
        this.tx_list.add(coinbase_tx);
        this.merkle_list = new ArrayList<String> ();
        this.makeMerkle();
    }
    public int addTX(Transaction new_tx) {
        this.tx_list.add(new_tx);
        makeMerkle();
        return 0;
    }
    public ArrayList<Transaction> getTXList() {
        return new ArrayList<Transaction>(this.tx_list);
    }
    public String getMerkleRoot() {
        return this.merkle_list.get(merkle_list.size() - 1);
    }
    public int makeMerkle() {
        this.merkle_list.clear();
        for (int i = 0; i < tx_list.size(); i += 2) {
            if (i + 1 == tx_list.size()) this.merkle_list.add(
                Hashing.getHash(
                    tx_list.get(i)
                )
            );
            else {
                this.merkle_list.add(
                    Hashing.getHash(
                        tx_list.get(i).add(
                            tx_list.get(i + 1)
                        )
                    )
                );
            }
        }
        int piv = 0;
        while (true) {
            int cSize = this.merkle_list.size();
            if (cSize == piv + 1) break;
            for (; piv < cSize; piv += 2) {
                if (piv + 1 == this.merkle_list.size()) {
                    this.merkle_list.add(
                        Hashing.getHash(
                            this.merkle_list.get(piv)
                        )
                    );
                    piv--;
                }
                else this.merkle_list.add(
                    Hashing.getHash(
                        this.merkle_list.get(piv) + this.merkle_list.get(piv + 1)
                    )
                );
            }
        }
        return 0;
    }
}

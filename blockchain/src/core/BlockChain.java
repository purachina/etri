package core;

import java.util.*;

import util.BlockChainIO;
import util.Init;
public class BlockChain {
    public static Block cblock;
    public static ArrayList<Block> blockchain;
    static boolean closechk = false;
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
    public static void main (String[] args) {
        Scanner sc = new Scanner(System.in);
        //System.out.println("Where is server?");
        //chkInit.execInit(sc.nextLine());
        if (BlockChainIO.fileCheck()) {
            blockchain = BlockChainIO.readBlockChain();
            for (int i = 0; i < blockchain.size(); i++) {
                Block tmpblock = new Block(blockchain.get(i));
                if (!tmpblock.equals(blockchain.get(i))) {
                    System.out.println("there is something problem");
                }
            }
        }
        else {
            blockchain = new ArrayList<Block>();
            blockchain.add(new Block());
        }
        cblock = blockchain.get(blockchain.size() - 1);
        new Thread() {
            public void run() {
                Transaction coinbase_tx = new Transaction();
                while (true) {
                    Block tmpblock = cblock.mine(coinbase_tx);
                    if (tmpblock == null) return;
                    blockchain.add(tmpblock);
                    cblock = tmpblock;
                    if (closechk == true) return;
                }
            }   
        }.start();
        new Thread() {
            public void run() {
                while (true) {
                    String payer, payee, amount, ans = "";
                    System.out.println(
                        "\n\nCheck the blockchain: chk\nAdd the transaction: add\nClose the Program: cls\nSave this blockchain: save"
                    );
                    ans = sc.next();
                    if (ans.equals("chk")) {
                        for (int i = 0; i < blockchain.size(); i++) {
                            String out = blockchain.get(i).getBlockInfo();
                            System.out.println(out);
                            System.out.println("===================================================");
                        }
                    }
                    else if (ans.equals("add")) {
                        System.out.print("Payer: ");
                        payer = sc.next();
                        System.out.print("Payee: ");
                        payee = sc.next();
                        System.out.print("Amount: ");
                        amount = sc.next();
                        Transaction tx = new Transaction(payer, payee, amount);
                        tx.getInfo();
                        cblock.addTX(tx);
                        for (int i = 0; i < blockchain.size(); i++) {
                            blockchain.get(i).getBlockInfo();
                        }
                    }
                    else if (ans.equals("cls")) {
                        closechk = true;
                        return;
                    }
                    else if (ans.equals("save")) {
                        synchronized(blockchain) {
                            util.BlockChainIO.saveBlockChain(blockchain);
                        }
                    }
                }
            }
        }.start();
        if (closechk) sc.close();
    }
}

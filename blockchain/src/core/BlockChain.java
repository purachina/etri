package core;

import java.util.*;
import util.Init;
public class BlockChain {
    public static Block cblock;
    public static ArrayList<Block> blockchain;
    public static int setBlockchain(ArrayList<Block> nbc) {
        synchronized(blockchain) {
            ArrayList<Block> tmpbc = new ArrayList<Block>();
            for (int i = 0; i < nbc.size(); i++) {
                tmpbc.add(new Block(nbc.get(i)));
            }
            blockchain = tmpbc;
        }
        return 0;
    }
    public static void main (String[] args) {
        Scanner sc = new Scanner(System.in);
        //System.out.println("Where is server?");
        //chkInit.execInit(sc.nextLine());
        blockchain = new ArrayList<Block>();
        blockchain.add(new Block());
        cblock = blockchain.get(blockchain.size() - 1);
        new Thread() {
            public void run() {
                while (true) {
                    blockchain.add(cblock.mine());
                    cblock = blockchain.get(blockchain.size() - 1);
                    
                }
            }   
        }.start();
        new Thread() {
            public void run() {
                while (true) {
                    String payer, payee = "";
                    double amount = 0;
                    System.out.println(
                        "\n\nCheck the blockchain: chk\nAdd the transaction: add"
                    );
                    payer = sc.next();
                    if (payer.equals("chk")) {
                        for (int i = 0; i < blockchain.size(); i++) {
                            blockchain.get(i).getBlockInfo();
                        }
                    }
                    else if (payer.equals("add")) {
                        System.out.print("Payer: ");
                        payer = sc.next();
                        System.out.print("Payee: ");
                        payee = sc.next();
                        System.out.print("Amount: ");
                        amount = sc.nextDouble();
                        Transaction tx = new Transaction(payer, payee, amount);
                        tx.getInfo();
                        cblock.addTX(tx);
                        for (int i = 0; i < blockchain.size(); i++) {
                            blockchain.get(i).getBlockInfo();
                        }
                    }
                }
            }
        }.start();
    }
}

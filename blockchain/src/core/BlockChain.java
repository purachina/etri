package core;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import util.Network;
public class BlockChain {
    private static Block Cblock;
    private static ArrayList<Block> blockchain;
    private int init() {
        System.out.println("Where is server?");
        return 1;
    }
    public static void main (String[] args) {
        blockchain = new ArrayList<Block>();
        Scanner sc = new Scanner(System.in);
        blockchain.add(new Block());
        Cblock = blockchain.get(blockchain.size() - 1);
        
        new Thread() {
            public void run() {
                while (true) {
                    blockchain.add(Cblock.mine());
                    Cblock = blockchain.get(blockchain.size() - 1);
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
                        Cblock.addTX(tx);
                        for (int i = 0; i < blockchain.size(); i++) {
                            blockchain.get(i).getBlockInfo();
                        }
                    }
                }
            }
        }.start();
    }
}

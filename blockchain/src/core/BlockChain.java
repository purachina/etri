package core;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.*;
import util.Communicate;
public class BlockChain {
    public static Block Cblock;
    public static ArrayList<Block> blockchain;
    private static Communicate net;
    private static int init() {
        while (true) {
            System.out.println("Where is server?");
            Scanner sc = new Scanner(System.in);
            String server_addr = sc.nextLine();
            System.out.println("Are you full node or light node?");
            String full_or_light = sc.nextLine();
            if (full_or_light.equals("full") || full_or_light.equals("light")) {
                System.out.println("try again");
            }
            else {
                net = new Communicate(server_addr, full_or_light);
                break;
            }
        }
        return 1;
    }
    public static void main (String[] args) {
        Scanner sc = new Scanner(System.in);
        blockchain = new ArrayList<Block>();
        blockchain.add(new Block());
        Cblock = blockchain.get(blockchain.size() - 1);
        init();
        new Thread() {
            public void run() {
                while (true) {
                    blockchain.add(Cblock.mine());
                    Cblock = blockchain.get(blockchain.size() - 1);
                }
            }   
        }.start();

        ServerSocket server_sock = new ServerSocket(55555);
        new Thread() {
            public void run() {
                
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

package util;

import java.util.Scanner;

import core.BlockChain;
import core.Transaction;

public class UserControl extends Thread {
    public static boolean closechk = false;
    private static Scanner sc = new Scanner(System.in);
    public static String getServerIP() {
        System.out.println("Where is server?");
        return sc.nextLine();
    }
    public void run() {
        while (true) {
            String payer, payee, amount, ans = "";
            System.out.println("\n\nCheck the blockchain: chk\nAdd the transaction: add\nClose the Program: cls\nSave this blockchain: save");
            ans = sc.next();
            if (ans.equals("chk")) {
                BlockChain.printBlockChain();
                Communicate.printNodeList();
            }
            else if (ans.equals("add")) {
                System.out.print("Payer: ");
                payer = sc.next();
                System.out.print("Payee: ");
                payee = sc.next();
                System.out.print("Amount: ");
                amount = sc.next();
                Transaction tx = new Transaction(payer, payee, amount);
                BlockChain.addTX(tx);
            } 
            else if (ans.equals("cls")) {
                closechk = true;
                sc.close();
                return;
            }
            else if (ans.equals("save")) {
                SerialIO.savebin(BlockChain.getBlockChain(), Communicate.getNodeList());
            }
        }
    }
}

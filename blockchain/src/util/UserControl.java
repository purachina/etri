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
    public static String getBCID() {
        System.out.println("Enter the Blockchain ID");
        return sc.nextLine();
    }
    public static boolean isManu() {
        System.out.println("Are you Manufacturer? Y/N");
        String ans = sc.nextLine();
        if (ans.equals("Y") || ans.equals("y")) return true;
        return false;
    }
    public void run() {
        while (true) {
            String payer, payee, amount, ans = "";
            System.out.println("\n\nCheck the blockchain: chk\nHack the transaction: hack\nClose the Program: cls\nSave this blockchain: save");
            ans = sc.next();
            if (ans.equals("chk")) {
                System.out.println("Enter the Blockchain ID");
                System.out.println(BlockChain.getBCDict().keySet());
                ans = sc.next();
                BlockChain.printBlockChain(ans);
                Communicate.printNodeList(ans);
            }/*
            else if (ans.equals("add")) {
                System.out.print("Payer: ");
                payer = sc.next();
                System.out.print("Payee: ");
                payee = sc.next();
                System.out.print("Amount: ");
                amount = sc.next();
                Transaction tx = new Transaction(payer, payee, amount);
                BlockChain.addTX(tx);
            } */
            else if (ans.equals("cls")) {
                closechk = true;
                sc.close();
                return;
            }
            else if (ans.equals("hack")) {
                System.out.println("Enter the Blockchain ID");
                System.out.println(BlockChain.getBCDict().keySet());
                String bcid = sc.next();
                System.out.println("Enter the Block ID");
                BlockChain.printBlockChain(bcid);
                String bid = sc.next();
                Hack.hack(bcid, bid);
            }
            else if (ans.equals("save")) {
                SerialIO.savebin(BlockChain.getBCDict(), Communicate.getNodeDict());
            }
        }
    }
}

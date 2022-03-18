package test;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class test2 {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        System.out.println("where is server?");
        String ip = sc.nextLine();
        sc.close();
        try {
            Socket sock = new Socket(ip, 55555);
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            ArrayList<test3> blockchain = (ArrayList<test3>)ois.readObject();
            for (int i = 0; i < blockchain.size(); i++) {
                blockchain.get(i).showContents();
            }
            sock.close();
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

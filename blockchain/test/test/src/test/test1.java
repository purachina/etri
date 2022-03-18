package test;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class test1 {
    public static void sendBlockchain(ArrayList<test3> blockchain, Socket client_sock) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(client_sock.getOutputStream());
            oos.writeObject(blockchain);
            oos.flush();
            oos.close();
            client_sock.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void main(String args[]) {
        ArrayList<test3> blockchain = new ArrayList<test3>();
        blockchain.add(new test3());
        test3 second = new test3();
        second.setContents(" again!");
        blockchain.add(second);
        try {
            ServerSocket server_sock = new ServerSocket(55555);
            Socket client_sock;
            while(true) {
                client_sock = server_sock.accept();
                if (client_sock.equals(null) == false) {
                    sendBlockchain(blockchain, client_sock);
                    break;
                }
            }
            server_sock.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
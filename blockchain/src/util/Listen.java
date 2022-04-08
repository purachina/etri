package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import core.BlockChain;
public class Listen extends Thread {
    ServerSocket server_sock;
    Socket socket;
    PrintWriter pw;
    BufferedReader br;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    public Listen() {
        try {
            server_sock = new ServerSocket(55555);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void run() {
        while (true) {
            try {
                server_sock.setSoTimeout(1000);
                socket = server_sock.accept();
                socket.setSoTimeout(10000);
                pw = new PrintWriter(socket.getOutputStream());
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                System.out.println(socket.getInetAddress().getHostAddress());
                String needs = Communicate.ansHandshaking(socket, pw, br);
                System.out.println("sending...");
                String bcid = needs.split("-")[0];
                if (needs.contains("hash-")) {
                    needs = needs.split("-")[2];
                    if (needs.equals("blockchain")) {
                        Communicate.sendSomething(socket, bcid, Hashing.makeHash(BlockChain.getBlockChain(bcid)), oos);
                    }
                    else if (needs.equals("block")) {
                        Communicate.sendSomething(socket, bcid, BlockChain.getCurrentBlock(bcid).getBlockHash(), oos);
                    }
                    else if (needs.equals("nodelist")) {
                        Communicate.sendSomething(socket, bcid, Hashing.makeHash(Communicate.getNodeList(bcid)), oos);
                    }
                }
                else {
                    needs = needs.split("-")[1];
                    if (needs.equals("blockchain")) {
                        Communicate.sendSomething(socket, bcid, BlockChain.getBlockChain(bcid), oos);
                    }
                    else if (needs.equals("block")) {
                        Communicate.sendSomething(socket, bcid, BlockChain.getCurrentBlock(bcid), oos);
                    }
                    else if (needs.equals("nodelist")) {
                        System.out.println("send node list start!");
                        Communicate.sendSomething(socket, bcid, Communicate.getNodeList(bcid), oos);
                    }
                    else if (needs.equals("sendblock")) {
                        Object recv = Communicate.recvSomething(socket, ois);
                        BlockChain.acceptBlock(bcid, recv);
                        /*
                        if (BlockChain.acceptBlock(recv) == 0) {
                            Communicate.reqHandshaking(socket, "accept", pw, br);
                        }
                        else Communicate.reqHandshaking(socket, "no", pw, br);
                        */
                    }
                    else if (needs.equals("sendtx")) {
    
                    }
                }
                pw.close();
                br.close();
                oos.close();
                ois.close();
                socket.close();
                } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            if (UserControl.closechk == true) {
                break;
            }
        }
    }
}

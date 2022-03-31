package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                socket = server_sock.accept();
                socket.setSoTimeout(10000);
                pw = new PrintWriter(socket.getOutputStream());
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println(socket.getInetAddress().getHostAddress());
                String needs = Communicate.ansHandshaking(socket, pw, br);
                if (needs.contains("hash-")) {
                    needs = needs.split("-")[1];
                    if (needs.equals("blockchain")) {
                        Communicate.sendSomething(socket, Hashing.makeHash(BlockChain.getBlockChain()));
                    }
                    else if (needs.equals("block")) {
                        Communicate.sendSomething(socket, BlockChain.getCurrentBlock().getBlockHash());
                    }
                    else if (needs.equals("nodelist")) {
                        Communicate.sendSomething(socket, Hashing.makeHash(Communicate.getNodeList()));
                    }
                }
                else if (needs.equals("blockchain")) {
                    Communicate.sendSomething(socket, BlockChain.getBlockChain());
                }
                else if (needs.equals("block")) {
                    Communicate.sendSomething(socket, BlockChain.getCurrentBlock());
                }
                else if (needs.equals("nodelist")) {
                    System.out.println("send node list start!");
                    Communicate.sendSomething(socket, Communicate.getNodeList());
                }
                } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

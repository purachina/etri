package util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import core.BlockChain;
public class Listen extends Thread {
    private class SenderTh extends Thread {
        Socket socket;
        public SenderTh(Socket socket) {
            this.socket = socket;
        }
        public void run() {
            String needs = Communicate.ansHandshaking(socket);
            if (needs.contains("hash-")) {
                needs = needs.split("-")[1];
                if (needs.equals("blockchain")) {
                    Communicate.sendSomething(socket, Hashing.getHash(BlockChain.blockchain));
                }
                else if (needs.equals("block")) {
                    Communicate.sendSomething(socket, Hashing.getHash(BlockChain.cblock.getBlock()));
                }
                else if (needs.equals("nodelist")) {
                    Communicate.sendSomething(socket, Hashing.getHash(Communicate.getNodeList()));
                }
            }
            else if (needs.equals("blockchain")) {
                Communicate.sendSomething(socket, BlockChain.blockchain);
            }
            else if (needs.equals("block")) {
                Communicate.sendSomething(socket, BlockChain.cblock);
            }
            else if (needs.equals("nodelist")) {
                Communicate.sendSomething(socket, Communicate.getNodeList());
            }
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    ServerSocket server_sock;
    Socket socket;
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
                SenderTh sender = new SenderTh(socket);
                sender.start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

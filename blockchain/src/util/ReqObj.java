package util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import core.Block;

public class ReqObj {
    private static Socket makeSocket(String ip) {
        SocketAddress sock_addr = new InetSocketAddress(ip, 55555);
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(10000);
            socket.connect(sock_addr, 5000);
            return socket;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                Communicate.removeNode(ip);
            }
        }
        return null;
    }
    protected static ArrayList<Block> reqBlockchain(String ip) {
        Socket socket = makeSocket(ip);
        if (socket == null) return null;
        if (Communicate.reqHandshaking(socket, "blockchain").equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (recv instanceof ArrayList) {
                if (((ArrayList)recv).get(0) instanceof Block) {
                    return (ArrayList<Block>)recv;
                }
            }
        }
        return null;
    }
    protected static Block reqBlock(String ip) {
        Socket socket = makeSocket(ip);
        if (socket == null) return null;
        if (Communicate.reqHandshaking(socket, "block").equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (recv instanceof Block) return (Block)recv;
        }
        return null;
    }
    protected static ArrayList<String> reqNodeList(String ip) {
        Socket socket = makeSocket(ip);
        if (socket == null) return null;
        if (Communicate.reqHandshaking(socket, "nodelist").equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (recv instanceof ArrayList) {
                if (((ArrayList)recv).get(0) instanceof String) {
                    return (ArrayList<String>)recv;
                }
            }
        }
        return null;
    }
    protected static String reqHash(String ip, String needs) {
        Socket socket = makeSocket(ip);
        String ans = "";
        if (socket == null) return null;
        if (needs.equals("blockchain")) {
            ans = Communicate.reqHandshaking(socket, "hash-blockchain");
        }
        else if (needs.equals("block")) {
            ans = Communicate.reqHandshaking(socket, "hash-block");
        }
        else if (needs.equals("nodelist")) {
            ans = Communicate.reqHandshaking(socket, "hash-nodelist");
        }
        if (ans.equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (recv instanceof String) {
                return (String) recv;
            }
        }
        return null;
    }
    protected static class ReqThread extends Thread {
        String tar_ip, needs, ans;
        Object recv;
        public ReqThread(String tar_ip, String needs) {
            this.tar_ip = new String(tar_ip);
            this.needs = new String(needs);
        }
        public void run() {
            if (needs.contains("hash-")) {
                recv = reqHash(tar_ip, needs);
                if (recv != null && recv instanceof String) {
                    Consensus.addHash((String)recv, tar_ip);
                }
            }
            else if (needs.equals("blockchain")) {
                recv = reqBlockchain(tar_ip);
                if (recv != null && recv instanceof ArrayList) {
                    if (((ArrayList)recv).get(0) instanceof Block) {

                    }
                }
            }
            else if (needs.equals("block")) {
                recv = reqBlock(tar_ip);
            }
            else if (needs.equals("nodelist")) {
                recv = reqNodeList(tar_ip);
            }
        }
    }
}
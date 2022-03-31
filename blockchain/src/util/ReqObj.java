package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import core.Block;

public class ReqObj {
    private static PrintWriter pw;
    private static BufferedReader br;
    private static Socket makeSocket(String ip) {
        SocketAddress sock_addr = new InetSocketAddress(ip, 55555);
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(10000);
            socket.connect(sock_addr, 5000);
            pw = new PrintWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        if (Communicate.reqHandshaking(socket, "blockchain", pw, br).equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            System.out.println(recv.getClass().getName());
            System.out.println((((ArrayList)recv).get(0)).getClass().getName());
            try {
                pw.close();
                br.close();
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
        if (Communicate.reqHandshaking(socket, "block", pw, br).equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            try {
                pw.close();
                br.close();
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
        if (Communicate.reqHandshaking(socket, "nodelist", pw, br).equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            try {
                System.out.println("closing socket");
                pw.close();
                br.close();
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (recv instanceof ArrayList) {
                if (((ArrayList)recv).get(0) instanceof String) {
                    ArrayList<String>tmp = (ArrayList<String>)recv;
                    System.out.println("this is received stuff");
                    for (int i = 0; i < tmp.size(); i++) System.out.println(tmp.get(i));
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
            ans = Communicate.reqHandshaking(socket, "hash-blockchain", pw, br);
        }
        else if (needs.equals("block")) {
            ans = Communicate.reqHandshaking(socket, "hash-block", pw, br);
        }
        else if (needs.equals("nodelist")) {
            ans = Communicate.reqHandshaking(socket, "hash-nodelist", pw, br);
        }
        if (ans.equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            try {
                pw.close();
                br.close();
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

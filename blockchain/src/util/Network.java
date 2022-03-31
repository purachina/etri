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
import core.Transaction;

public class Network {
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
        if (socket == null) return null;
        String ans = Communicate.reqHandshaking(socket, needs, pw, br);
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
        else {
            try {
                pw.close();
                br.close();
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    protected static class ReqThread extends Thread {
        String target_ip, needs, ans;
        Object recv;
        public ReqThread(String target_ip, String needs) {
            this.target_ip = new String(target_ip);
            this.needs = new String(needs);
        }
        public void run() {
            if (target_ip.equals(Communicate.myip)) return;
            System.out.println(target_ip + " " + needs);
            if (needs.contains("hash-")) {
                recv = reqHash(target_ip, needs);
                if (recv != null && recv instanceof String) {
                    System.out.println((String)recv + " " + target_ip);
                    Consensus.addHash((String)recv, target_ip);
                }
            }
            else if (needs.equals("blockchain")) {
                recv = reqBlockchain(target_ip);
                if (recv != null && recv instanceof ArrayList) {
                    if (((ArrayList)recv).get(0) instanceof Block) {

                    }
                }
            }
            else if (needs.equals("block")) {
                recv = reqBlock(target_ip);
            }
            else if (needs.equals("nodelist")) {
                recv = reqNodeList(target_ip);
            }
        }
    }

        
    protected static int sendBlock(String ip, ArrayList<Block> newblocks) {
        Socket socket = makeSocket(ip);
        if (socket == null) return 1;
        String ans = Communicate.reqHandshaking(socket, "sendblock", pw, br);
        if (ans.equals("OK")) {
            Communicate.sendSomething(socket, newblocks);
            try {
                pw.close();
                br.close();
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 0;
        }
        else {
            try {
                pw.close();
                br.close();
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 1;
    }

    public static class DistributeBlockThread extends Thread {
        private String target_ip;
        private ArrayList<Block> newblocks;
        public DistributeBlockThread(String target_ip, Block target_block, Block next_block) {
            this.target_ip = target_ip;
            newblocks = new ArrayList<Block>();
            newblocks.add(target_block);
            newblocks.add(next_block);
        }
        public void run() {
            if (target_ip.equals(Communicate.myip)) return;
            sendBlock(target_ip, newblocks);
        }
    }
}

package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;
    private static Socket makeSocket(String ip) throws IOException {
        SocketAddress sock_addr = new InetSocketAddress(ip, 55555);
        Socket socket = new Socket();
        socket.setSoTimeout(10000);
        socket.connect(sock_addr, 5000);
        pw = new PrintWriter(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        return socket;
        /*
        try {
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                try {
                    socket.setSoTimeout(10000);
                    socket.connect(sock_addr, 5000);
                    pw = new PrintWriter(socket.getOutputStream());
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    return socket;
                } catch (IOException ee) {
                    // TODO Auto-generated catch block
                    ee.printStackTrace();
                    if (ee instanceof SocketException || ee instanceof SocketTimeoutException) Communicate.removeNode(ip);
                }
            }
        }
            */
    }
    protected static ArrayList<Block> reqBlockchain(String ip, String bcid) {
        Socket socket;
        for (int i = 0; i < 3; i++) {
            try {
                System.out.println("request blockchain to " + ip);
                socket = makeSocket(ip);
                pw = new PrintWriter(socket.getOutputStream());
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if (Communicate.reqHandshaking(socket, bcid + "-blockchain", pw, br).equals("OK")) {
                    Object recv = Communicate.recvSomething(socket, ois);
                    try {
                        pw.close();
                        br.close();
                        oos.close();
                        ois.close();
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (recv instanceof ArrayList) {
                        if (((ArrayList) recv).get(0) instanceof Block) {
                            System.out.println("blockchain received!!!");
                            return (ArrayList<Block>) recv;
                        }
                    }
                }
            } catch (IOException e) {
                if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                    try {
                        System.out.println("receiving blockchain has failed... will try again after 512mils");
                        Thread.sleep(512);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("requesting blockchain has fatal error!!!");
        }
        return null;
    }
    protected static Block reqBlock(String ip, String bcid) {
        for (int i = 0; i < 3; i++) {
            Socket socket;
            try {
                System.out.println("request block to " + ip);
                socket = makeSocket(ip);
                pw = new PrintWriter(socket.getOutputStream());
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if (Communicate.reqHandshaking(socket, bcid + "-block", pw, br).equals("OK")) {
                    Object recv = Communicate.recvSomething(socket, ois);
                    try {
                        pw.close();
                        br.close();
                        oos.close();
                        ois.close();
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (recv instanceof Block) {
                        System.out.println("block received!!!");
                        ((Block)recv).printBlock();
                        return (Block)recv;
                    } 
                }
            } catch (IOException e) {
                if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                    try {
                        System.out.println("receiving block has failed... will try again after 512mils");
                        Thread.sleep(512);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("requesting block has fatal error!!!");
        }
        return null;
    }
    protected static ArrayList<String> reqNodeList(String ip, String bcid) {
        for (int i = 0; i < 3; i++) {
            Socket socket;
            try {
                System.out.println("request nodelist to " + ip);
                socket = makeSocket(ip);
                pw = new PrintWriter(socket.getOutputStream());
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if (Communicate.reqHandshaking(socket, bcid + "-nodelist", pw, br).equals("OK")) {
                    Object recv = Communicate.recvSomething(socket, ois);
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
                        if (((ArrayList) recv).get(0) instanceof String) {
                            ArrayList<String> tmp = (ArrayList<String>) recv;
                            System.out.println("nodelist received!!!");
                            for (int j = 0; j < tmp.size(); j++)
                                System.out.println(tmp.get(j));
                            return (ArrayList<String>) recv;
                        }
                    }
                }
            } catch (IOException e) {
                if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                    try {
                        System.out.println("receiving nodelist has failed... will try again after 512mils");
                        Thread.sleep(512);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("requesting nodelist has fatal error!!!");
        }
        return null;
    }
    protected static String reqHash(String ip, String needs, String bcid) {
        for (int i = 0; i < 3; i++) {
            Socket socket;
            try {
                System.out.println("request hash to " + ip);
                socket = makeSocket(ip);
                String ans = Communicate.reqHandshaking(socket, bcid + "-" + needs, pw, br);
                if (ans.equals("OK")) {
                    Object recv = Communicate.recvSomething(socket, ois);
                    try {
                        pw.close();
                        br.close();
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (recv instanceof String) {
                        System.out.println("hash received!!!");
                        return (String) recv;
                    }
                }
                else {
                    try {
                        pw.close();
                        br.close();
                        oos.close();
                        ois.close();
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                    try {
                        Thread.sleep(512);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    protected static class ReqThread extends Thread {
        String target_ip, needs, ans, bcid;
        Object recv;
        public ReqThread(String target_ip, String needs, String bcid) {
            this.target_ip = new String(target_ip);
            this.needs = new String(needs);
            this.bcid = new String(bcid);
        }
        public void run() {
            System.out.println("request " + needs + " to target_ip");
            if (target_ip.equals(Communicate.myip)) return;
            System.out.println(target_ip + " " + needs);
            if (needs.contains("hash-")) {
                recv = reqHash(target_ip, needs, bcid);
                if (recv != null && recv instanceof String) {
                    Consensus.addHash((String)recv, target_ip);
                }
            }
            else if (needs.equals("blockchain")) {
                recv = reqBlockchain(target_ip, bcid);
                if (recv != null && recv instanceof ArrayList) {
                    if (((ArrayList)recv).get(0) instanceof Block) {

                    }
                }
            }
            else if (needs.equals("block")) {
                recv = reqBlock(target_ip, bcid);
            }
            else if (needs.equals("nodelist")) {
                recv = reqNodeList(target_ip, bcid);
            }
        }
    }

        
    protected static int sendBlock(String ip, String bcid, ArrayList<Block> newblocks) {
        System.out.println("send block to " + ip);
        for (int i = 0; i < 3; i++) {
            Socket socket;
            try {
                socket = makeSocket(ip);
                String ans = Communicate.reqHandshaking(socket, bcid + "-sendblock", pw, br);
                if (ans.equals("OK")) {
                    Communicate.sendSomething(socket, bcid, newblocks, oos);
                    /*socket = makeSocket(ip);
                    ans = Communicate.ansHandshaking(socket, pw, br);
                    if (ans.equals("accept")) {
                        System.out.println(socket.getInetAddress().getHostAddress() + " says yes");
                        Consensus.powAccept();
                    }
                    else if (ans.equals("no")) {
                        Consensus.powDeny();
                        System.out.println(socket.getInetAddress().getHostAddress() + " says no");
                    }*/
                    try {
                        pw.close();
                        br.close();
                        ois.close();
                        oos.close();
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
            } catch (IOException e) {
                if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                    try {
                        Thread.sleep(512);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 1;
    }

    public static class DistributeBlockThread extends Thread {
        private String target_ip, bcid;
        private ArrayList<Block> newblocks;
        public DistributeBlockThread(String target_ip, String bcid, Block target_block, Block next_block) {
            this.target_ip = target_ip;
            this.bcid = bcid;
            newblocks = new ArrayList<Block>();
            newblocks.add(target_block);
            newblocks.add(next_block);
        }
        public void run() {
            if (target_ip.equals(Communicate.myip)) return;
            sendBlock(target_ip, bcid, newblocks);
        }
    }
}

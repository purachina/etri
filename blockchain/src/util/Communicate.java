package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import core.Block;
import core.Transaction;

public class Communicate {
    protected static ArrayList<String> node;
    // client func sets gonna be used in Network() constructor and
    // may use only once(when program starts)
    private int noticeType(Socket socket, Boolean is_full_node) {
        try {
            if (is_full_node.equals("full") || is_full_node.equals("light")) {
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.print(is_full_node);
                pw.flush();
                pw.close();
            }
            else {
                return 1;
            }
            return 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }
    protected static int sendSomething(Socket socket, Object o) {
        try {
            String new_node = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress();
            if (!node.contains(new_node)) {
                synchronized(node) {
                    node.add(new_node);
                }
            }
            if ((o instanceof Block) || o instanceof Transaction ||
            o instanceof ArrayList && ((ArrayList)o).get(0) instanceof Block) {
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.print("sending object");
                pw.flush();
                pw.close();
                ObjectOutputStream oos =
                new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(o);
                oos.flush();
                oos.close();
                return 0;
            }
            else {
                socket.close();
                return 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
    // Distribution something on P2P network
    private class DOONThread implements Runnable {
        private String ip;
        private Object o;
        public DOONThread(String ip, Object o) {
            this.ip = ip;
            this.o = o;
        }
        @Override
        public void run() {
            SocketAddress sock_addr = new InetSocketAddress(ip, 55555);
            Socket socket = new Socket();
            try {
                socket.setSoTimeout(10000);
                socket.connect(sock_addr, 5000);
                if (sendSomething(socket, o) != 0) {
                    System.out.println("Distributing Error!!!");
                }
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public int distributeObjOnNetwork(Object o) {
        for (int i = 0; i < node_ip_and_stat_list.size(); i+=2) {
            if (node_ip_and_stat_list.get(i + 1).equals("full")) {
                Thread t = new Thread(
                    new DOONThread(
                        node_ip_and_stat_list.get(i), o
                    )
                );
                t.start();
            }
        }
        return 0;
    }
    protected static Object recvSomething(Socket socket) {
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(
                        socket.getInputStream());
                Object recv_item = ois.readObject();
                return recv_item;
            } catch (IOException | ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    protected static String ansHandshaking(Socket socket) {
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()
                )
            );
            String ans = null;
            if (br.readLine().equals("asdf")) {
                pw.print("OK");
                pw.flush();
                ans = br.readLine();
            }
            pw.close();
            br.close();
            return ans;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    protected static String reqHandshaking(Socket socket, String tar) {
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()
                )
            );
            pw.print("asdf");
            pw.flush();
            if (br.readLine().equals("OK")) {
                pw.print(tar);
                pw.flush();
            }
            pw.close();
            br.close();
            return "OK";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tar;
    }
}

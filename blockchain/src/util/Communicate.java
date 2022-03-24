package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import core.Block;
import core.Transaction;

public class Communicate {
    private static ArrayList<String> node;
    private static ArrayList<String> recv_hash;
    protected static int removeNode(String target) {
        synchronized(node) {
            node.remove(target);
        }
        return 0;
    }
    protected static int addNode(String target) {
        synchronized(node) {
            if (node == null) node = new ArrayList<String>();
            node.add(new String(target));
        }
        return 0;
    }
    protected static int copyNodeList(ArrayList<String> target) {
        synchronized(node) {
            if (node == null) node = new ArrayList<String>();
            node.clear();
            for (int i = 0; i < target.size(); i++) {
                node.add(new String(target.get(i)));
            }
        }
        return 0;
    }
    protected static ArrayList<String> getNodeList() {
        ArrayList<String> tmp = new ArrayList<String>();
        synchronized(node) {
            for (int i = 0; i < node.size(); i++) {
                tmp.add(node.get(i));
            }
        }
        return tmp;
    }
    protected static int initNodeList() {
        node = new ArrayList<String>();
        return 0;
    }

    protected static int sendSomething(Socket socket, Object o) {
        try {
            String new_node = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress();
            if (!node.contains(new_node)) {
                synchronized(node) {
                    node.add(new_node);
                }
            }
            if (o instanceof Block || o instanceof Transaction ||
            o instanceof String ||
            o instanceof ArrayList && ((ArrayList)o).get(0) instanceof Block) {
                PrintWriter pw = new PrintWriter(socket.getOutputStream());BufferedReader br =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pw.print("sending object");
                pw.flush();
                pw.close();
                if (br.readLine().equals("gotit")) {
                    ObjectOutputStream oos =
                    new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(o);
                    oos.flush();
                    oos.close();
                }
                return 0;
            }
            else {
                System.out.println("you cannot send this obj");
                socket.close();
                return 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("something was wrong with send process");
        return 1000;
    }
    protected static Object recvSomething(Socket socket) {
        Object recv_item = "";
        try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                BufferedReader br =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if (br.readLine().equals("sending object")) {
                    pw.print("gotit");
                    pw.flush();
                    ObjectInputStream ois =
                    new ObjectInputStream(socket.getInputStream());
                    recv_item = ois.readObject();
                }
                pw.close();
                br.close();
            } catch (IOException | ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        if (recv_item.equals("")) System.out.println("System does not received anything");
        return recv_item;
    }

    protected static String ansHandshaking(Socket socket) {
        String ans = "";
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            BufferedReader br =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        System.out.println("AH error");
        return ans;
    }
    protected static String reqHandshaking(Socket socket, String tar) {
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            BufferedReader br =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
        System.out.println("RH error");
        return tar;
    }
}

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
import java.util.HashMap;

import core.Block;
import core.Transaction;

public class Communicate {
    //private static ArrayList<String> node = new ArrayList<String>();
    private static HashMap<String, ArrayList<String>> nodedict = new HashMap<String, ArrayList<String>>();
    private static ArrayList<String> node;
    public static String myip;
    private static void setWorkspace(String bcid) {
        if (nodedict == null) {
            nodedict = new HashMap<String, ArrayList<String>>();
        }
        if (nodedict.containsKey(bcid)) {
            node = nodedict.get(bcid);
        }
        else {
            node = new ArrayList<String>();
            nodedict.put(bcid, node);
        }
    }
    /*protected static int removeNode(String target) {
        synchronized(node) {
            node.remove(target);
        }
        return 0;
    }*/
    protected static int addNode(String bcid, String targetip) {
        synchronized(nodedict) {
            setWorkspace(bcid);
            node.add(new String(targetip));
        }
        return 0;
    }/*
    protected static int addNode(String target) {
        synchronized(node) {
            setWorkspace(bcid);
            if (node == null) node = new ArrayList<String>();
            node.add(new String(target));
        }
        return 0;
    }*/
    protected static int setNodeDict(String bcid, ArrayList<String> recv) {
        synchronized(nodedict) {
            if (nodedict == null) {
                nodedict = new HashMap<String, ArrayList<String>>();
            }
            ArrayList<String> tmp = new ArrayList<String>();
            for (int i = 0; i < recv.size(); i++) tmp.add(recv.get(i));
            nodedict.put(bcid, tmp);
        }
        return 0;
    }/*
    protected static int setNodeList(ArrayList<String> target) {
        synchronized(node) {
            if (node == null) node = new ArrayList<String>();
            node.clear();
            for (int i = 0; i < target.size(); i++) {
                node.add(new String(target.get(i)));
            }
        }
        return 0;
    }*/
    protected static int loadNodeDict(HashMap<String, ArrayList<String>> newnd) {
        synchronized(nodedict) {
            String [] bcids = newnd.keySet().toArray(new String[newnd.size()]);
            for (int i = 0; i < bcids.length; i++) {
                setNodeDict(bcids[i], newnd.get(bcids[i]));
            }
        }
        return 0;
    }
    public static ArrayList<String> getNodeList(String bcid) {
        ArrayList<String> tmp = null;
        synchronized(nodedict) {
            setWorkspace(bcid);
            tmp = new ArrayList<String>();
            for (int i = 0; i < node.size(); i++) {
                tmp.add(node.get(i));
            }
        }
        return tmp;
    }
    public static HashMap<String, ArrayList<String>> getNodeDict() {
        HashMap<String, ArrayList<String>> ret = null;
        synchronized(nodedict) {
            ret = new HashMap<String, ArrayList<String>>();
            String [] bcids = nodedict.keySet().toArray(new String[nodedict.size()]);
            for (int i = 0; i < bcids.length; i++) {
                ret.put(bcids[i], nodedict.get(bcids[i]));
            }
        }
        return ret;
    }
    public static int printNodeList(String bcid) {
        synchronized(nodedict) {
            setWorkspace(bcid);
            for (int i = 0; i < node.size(); i++) {
                System.out.println(node.get(i));
            }
        }
        return 0;
    }
    protected static int sendSomething(Socket socket, String bcid, Object o, ObjectOutputStream oos) {
        try {
            String newnode = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress();
            String ans;
            synchronized(nodedict) {
                setWorkspace(bcid);
                if (!node.contains(newnode)) node.add(newnode);
            }
            if (o instanceof Block || o instanceof Transaction || o instanceof String || o instanceof ArrayList && ((ArrayList)o).get(0) instanceof Block || o instanceof ArrayList && ((ArrayList)o).get(0) instanceof String) {
                /*PrintWriter pw = new PrintWriter(socket.getOutputStream());BufferedReader br =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pw.println("sending object");
                pw.flush();
                pw.close();
                ans = "";
                while(true) {
                    ans = br.readLine();
                    if (ans.length() > 0) break;
                }
                System.out.println(ans);
                if (ans.equals("gotit")) {
                    ObjectOutputStream oos =
                    new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(o);
                    oos.flush();
                    oos.close();
                }*/
                oos.writeObject(o);
                oos.flush();
                System.out.println("Send " + o.getClass().getName() + " to " + newnode);
                return 0;
            }
            else {
                System.out.println("you cannot send this obj");
                return 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("something was wrong with send process");
        return 1000;
    }
    protected static Object recvSomething(Socket socket, ObjectInputStream ois) {
        Object recv_item = "";
        String ans;
        try {
                /*PrintWriter pw = new PrintWriter(socket.getOutputStream());
                BufferedReader br =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ans = "";
                while(true) {
                    ans = br.readLine();
                    if (ans.length() > 0) break;
                }
                System.out.println(ans);
                if (ans.equals("sending object")) {
                    pw.println("gotit");
                    pw.flush();
                    ObjectInputStream ois =
                    new ObjectInputStream(socket.getInputStream());
                    recv_item = ois.readObject();
                }
                pw.close();
                br.close();*/
                recv_item = ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        System.out.println("received " + recv_item.getClass().getName() + " from " + socket.getInetAddress().getHostAddress());
        if (recv_item.equals("")) System.out.println("System does not received anything");
        return recv_item;
    }

    protected static String ansHandshaking(Socket socket, PrintWriter pw, BufferedReader br) {
        String ans = "";
        try {
            ans = "";
            while(true) {
                ans = br.readLine();
                if (ans.length() > 0) break;
            }
            System.out.println(ans + "end");
            if (ans.equals("asdf")) {
                System.out.println("Handshake answer authed");
                pw.println("OK");
                pw.flush();
                ans = "";
                while(true) {
                    ans = br.readLine();
                    if (ans.length() > 0) break;
                }
                System.out.println(ans + " gonna be sending");
            }
            return ans;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("answer handshaking has an error");
        return ans;
    }
    protected static String reqHandshaking(Socket socket, String tar, PrintWriter pw, BufferedReader br) {
        try {
            String ans;
            pw.println("asdf");
            pw.flush();
            ans = "";
            System.out.println("request " + tar + " Handshaking...");
            while(true) {
                ans = br.readLine();
                if (ans.length() > 0) break;
            }
            if (ans.equals("OK")) {
                System.out.println(tar + " Handshake request authed");
                pw.println(tar);
                pw.flush();
            }
            return "OK";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("request handshaking has an error");
        return tar;
    }
    
}

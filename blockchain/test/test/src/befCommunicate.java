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
    protected static ArrayList<String> full_node;
    protected static ArrayList<String> light_node;
    public Communicate(String server_ip, String full_or_light) {
        full_node = new ArrayList<String>();
        light_node = new ArrayList<String>();
        full_node.add("full");
        light_node.add("light");
        connectInit(server_ip, full_or_light);
    }
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
    private ArrayList<Block> recvBlockchain(Socket socket) {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ArrayList<Block> recv_blockchain = (ArrayList<Block>)ois.readObject();
            return recv_blockchain;
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    private String recvHash(Socket socket) {
        try {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()
                )
            );
            String recv = br.readLine();
            br.close();
            return recv;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "error";
    }
    private int recvNodeList(Socket socket) {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ArrayList<String> recv_node_list = (ArrayList<String>)ois.readObject();
            for (int i = 0; i < recv_node_list.size(); i += 2) {
                if (this.node_ip_and_stat_list.indexOf(recv_node_list.get(i)) == -1) {
                    this.node_ip_and_stat_list.add(recv_node_list.get(i));
                    this.node_ip_and_stat_list.add(recv_node_list.get(i + 1));
                }
            }
            return 0;
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }


    // consensus, transaction distribution, etc 
    private boolean PoW(int recv_nonce, String recv_block_hash, Block my_block) {
        String my_hash =
        Hashing.getHash(my_block.getPreBlockHash() + recv_nonce + my_block.getMerkleRoot() + my_block.getTimestamp());
        if (my_hash.equals(recv_block_hash) && 
        my_hash.substring(
            0, my_block.getDifficulty().length()
        ).
        compareTo(my_block.getDifficulty()) <= 0) {
            return true;
        }
        return false;
    }

    protected static int sendSomething(Socket socket, Object o) {
        try {
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
    private class ROFNThread implements Runnable {
        private Object recv_item;

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
    protected static String handshaking(Socket socket) {
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
    protected static String handshaking(Socket socket, String tar) {
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
    // server func sets gonna be used in main()
    protected static String isFullClient(Socket client_sock) {
        // checking that the node is lightweight type or full type
        try {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    client_sock.getInputStream()
                )
            );
            String ans = br.readLine();
            br.close();
            if (ans.equals("full")) return "full";
            else if (ans.equals("light")) return "light";
            else return "err";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "err";
    }
    private int sendNodeList(Socket client_sock) {
        try {
            ObjectOutputStream oos =
            new ObjectOutputStream(client_sock.getOutputStream());
            oos.writeObject(node_ip_and_stat_list);
            oos.flush();
            oos.close();
            return 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }
    private int sendBlockchain(ArrayList<Block> blockchain, Socket client_sock) {
        try {
            ObjectOutputStream oos =
            new ObjectOutputStream(client_sock.getOutputStream());
            oos.writeObject(blockchain);
            oos.flush();
            oos.close();
            return 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }
    private int sendHash(String hash_value, Socket client_sock) {
        try {
            PrintWriter pw = new PrintWriter(client_sock.getOutputStream());
            pw.print(hash_value);
            pw.flush();
            pw.close();
            return 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }
    public int server(ArrayList<Block> blockchain) {
        // resident function which run in main() and
        // take over arg(blockchain) from main()
        // when newbee participated,
        // sends alive node's ip, whole blockchain(if needs), hash
        ServerSocket server_sock;
        try {
            server_sock = new ServerSocket(55555);
            while (true) {
                try (Socket client_sock = server_sock.accept()) {
                    SocketMng(client_sock);
                    // get external ip (Should gonna be test!)
                    String new_peer_addr =
                    ((InetSocketAddress)client_sock.getRemoteSocketAddress()).getAddress().getHostAddress();
                    

                    String chk = isFullClient(client_sock);
                    if (chk.equals("full") || nodechk.equals("light")) {
                        if (chk.equals("full")) {
                            sendBlockchain(blockchain, client_sock);
                        }
                        else if (chk.equals("light")) {
                            sendHash(Hashing.getHash(blockchain), client_sock);
                        }
                        if (this.node_ip_and_stat_list.indexOf(new_peer_addr) ==
                        -1) {
                            this.node_ip_and_stat_list.add(new_peer_addr);
                            this.node_ip_and_stat_list.add(chk);
                        }
                        sendNodeList(client_sock);
                    }
                    else if (chk.equals("sending object")) {
                        for (int i = 0; i < node_ip_and_stat_list.size() - 2;
                        i += 2) {
                            new Thread() {
                                public void run() {
                                    ServerSocket ss = new ServerSocket(55555);
                                    while (true) {
                                        
                                    }
                                }   
                            }.start();
                        }
                    }
                    else client_sock.close();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }
}

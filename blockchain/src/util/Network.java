package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import core.Block;
import core.Transaction;

public class Network {
    private ArrayList<String> node_ip_and_stat_list;

    // client func sets gonna be used in Network() constructor and
    // may use only once(when program starts)
    private int noticeType(Socket socket, boolean is_full_node) {
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            if (is_full_node) pw.print("full");
            else pw.print("light");
            pw.flush();
            pw.close();
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
    private int connectInit(String server_ip, boolean is_full_node) {
        // receive whole node's ip list and whole blockchain and hash from manager node
        SocketAddress sock_addr = new InetSocketAddress(server_ip, 55555);
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(10000);
            socket.connect(sock_addr, 5000);
            noticeType(socket, is_full_node);
            if (is_full_node) recvBlockchain(socket);
            else recvHash(socket);
            recvNodeList(socket);
            socket.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return 0;
    }


    // consensus, transaction distribution, etc 

    private int sendSomething(String recv_ip, Object o) {
        SocketAddress sock_addr = new InetSocketAddress(recv_ip, 55556);
        Socket socket = new Socket();
        try {
            if ((o instanceof Block) || o instanceof Transaction ||
            o instanceof ArrayList && ((ArrayList)o).get(0) instanceof Block) {
                socket.setSoTimeout(10000);
                socket.connect(sock_addr, 5000);
                ObjectOutputStream oos =
                new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(o);
                oos.flush();
                oos.close();
                socket.close();
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
    private ArrayList<Object> recvSomething(Socket socket) {
        if (socket.equals(null)) ; // 추가추가추가추가
        ServerSocket server_sock;
        try {
            server_sock = new ServerSocket(55556);
            while (true) {
                try (Socket socket = server_sock.accept()) {
                    if (!handshaking(false, socket)) socket.close();
                    else {
                        ObjectInputStream ois = new ObjectInputStream(
                            socket.getInputStream()
                        );
                        Object recv_item = ois.readObject();
                        String recv_type = "";
                        server_sock.close();
                        if (recv_item instanceof Block) recv_type = "Block";
                        else if (recv_item instanceof Transaction) {
                            recv_type = "Transaction";
                        }
                        else if (recv_item instanceof ArrayList &&
                        ((ArrayList) recv_item).get(0) instanceof Block) {
                            recv_type = "Blockchain";
                        }
                        else return null;
                        
                        ArrayList<Object> ret = new ArrayList<Object>();
                        ret.add(recv_type);
                        ret.add(recv_item);
                        return ret;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    private boolean handshaking(boolean is_sender, Socket socket) {
        if (is_sender) {
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.print("asdf");
                pw.flush();
                pw.close();
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream()
                    )
                );
                String ans = br.readLine();
                br.close();
                if (ans.equals("zxcv")) return true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            try {
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream()
                    )
                );
                String ans = br.readLine();
                br.close();
                if (ans.equals("asdf")) {
                    PrintWriter pw = new PrintWriter(socket.getOutputStream());
                    pw.print("zxcv");
                    pw.flush();
                    pw.close();
                }
                else return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }
    // server func sets gonna be used in main()
    private String isFullClient(Socket client_sock) {
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
                    // get external ip (Should gonna be test!)
                    String new_peer_addr =
                    (
                        (InetSocketAddress)client_sock.getRemoteSocketAddress()
                    ).getAddress().getHostAddress();
                    

                    String nodechk = isFullClient(client_sock);
                    if (nodechk.equals("full") || nodechk.equals("light")) {
                        if (nodechk.equals("full")) {
                            sendBlockchain(blockchain, client_sock);
                        }
                        else if (nodechk.equals("light")) {
                            sendHash(Hashing.getHash(blockchain), client_sock);
                        }
                        if (this.node_ip_and_stat_list.indexOf(new_peer_addr) == -1) {
                            this.node_ip_and_stat_list.add(new_peer_addr);
                            this.node_ip_and_stat_list.add(nodechk);
                        }
                        sendNodeList(client_sock);
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


    public Network(ArrayList<String> mngr_node_list, boolean is_full_node) {
        for (int i = 0; i < mngr_node_list.size(); i++) {
            connectInit(mngr_node_list.get(i), is_full_node);
        }
    }
}

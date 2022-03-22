package util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class Listener extends Thread {
    public void run() {
        ServerSocket server_sock = new ServerSocket(55555);
        while (true) {
            try (Socket socket = server_sock.accept()) {
                // get external ip (Should gonna be test!)
                String needs = Communicate.ansHandshaking(socket);
                if (needs.equals("nodelist")) {
                    Communicate.sendSomething(socket, Communicate.node);
                }
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
    }
}

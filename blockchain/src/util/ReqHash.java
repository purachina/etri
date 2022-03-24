package util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ReqHash extends Thread {
    String tar_ip, needs, ans;
    Object recv;
    public ReqHash(String tar_ip, String needs) {
        this.tar_ip = new String(tar_ip);
        this.needs = new String(needs);
    }
    public void run() {
        SocketAddress sock_addr = new InetSocketAddress(tar_ip, 55555);
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(10000);
            socket.connect(sock_addr, 5000);
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
                recv = Communicate.recvSomething(socket);
                if (recv instanceof String) {
                    Consensus.addHash((String)recv, tar_ip);
                }
            }
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (e instanceof SocketException || e instanceof SocketTimeoutException) {
                Communicate.removeNode(tar_ip);
            }
        }
    }
}

package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import core.Block;
import core.BlockChain;
import util.Network.ReqThread;

public class Init {
    public static int init() {
        try {
            Communicate.myip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (SerialIO.fileCheck()) {
            SerialIO.readbin();
        }
        else {
            String server_ip = UserControl.getServerIP();
            if (server_ip.equals("127.0.0.1") || server_ip.equals("")) {
                /*
                URL reqip;
                try {
                    reqip = new URL("http://checkip.amazonaws.com");
                    BufferedReader br;
                    br = new BufferedReader(new InputStreamReader(reqip.openStream()));
                    String myip = br.readLine();
                    System.out.println(myip);
                    Communicate.addNode(myip);
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                */
                Communicate.addNode(Communicate.myip);
                return 0;
            }
            else {
                ArrayList<String> tmpnode = Network.reqNodeList(server_ip);
                if (tmpnode == null) {
                    System.out.println("ip list request error");
                    return 1;
                }
                Communicate.setNodeList(tmpnode);
                ArrayList<Block> tmpbc = Network.reqBlockchain(server_ip);
                if (tmpbc == null) {
                    System.out.println("blockchain request error");
                    return 2;
                }
                ArrayList<String> iparr = Communicate.getNodeList();
                for (int i = 0; i < iparr.size(); i++) {
                    ReqThread rt = new ReqThread(iparr.get(i), "hash-blockchain");
                    rt.start();
                }
                try {
                    Thread.sleep(15100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String netres = Consensus.hashElection();
                String recvbchash = Hashing.makeHash(tmpbc);
                System.out.println(recvbchash);
                if (netres.equals(recvbchash)) {
                    BlockChain.setBlockchain(tmpbc);
                } else {
                    System.out.println("this server is lier");
                }
            }
        }
        return 0;
    }
}

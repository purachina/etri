package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import core.Block;
import core.BlockChain;

public class Init {
    public static int execInit(String server_ip) {
        // receive whole node's ip list and whole blockchain and hash from manager node
        if (server_ip.equals("127.0.0.1") || server_ip.equals("")) {
            Communicate.initNodeList();
            URL reqip;
            try {
                reqip = new URL("http://checkip.amazonaws.com");
                BufferedReader br;
                br = new BufferedReader(new InputStreamReader(reqip.openStream()));
                Communicate.addNode(br.readLine());
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 0;
        }
        
        SocketAddress sock_addr = new InetSocketAddress(server_ip, 55555);
        Socket socket = new Socket();

        try {
            socket.setSoTimeout(10000);
            socket.connect(sock_addr, 5000);
            ArrayList<String> tmpnode = ReqObj.reqNodeList(socket);
            if (tmpnode == null) {
                socket.close();
                return 1;
            }
            Communicate.copyNodeList(tmpnode);
            ArrayList<Block> tmpbc = ReqObj.reqBlockchain(socket);
            if (tmpbc == null) {
                socket.close();
                return 2;
            }
            BlockChain.blockchain = tmpbc;
            ArrayList<String> iparr = Communicate.getNodeList();
            for(int i = 0; i < iparr.size(); i++) {
                ReqHash rh = new ReqHash(iparr.get(i), "blockchain");
                rh.start();
            }
            try {
                Thread.sleep(15100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String netres = Consensus.hashElection();
            if (Auth.hashObjCompare(netres, tmpbc)) {
                
            }
            socket.close();
        }

        catch (IOException e) {e.printStackTrace();}
        return 0;
    }
}

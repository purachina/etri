package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;

public class Init {
    protected static int execInit(String server_ip, Boolean is_full_node) {
        // receive whole node's ip list and whole blockchain and hash from manager node
        if (server_ip.equals("127.0.0.1")) {
            URL reqip;
            try {
                reqip = new URL("http://checkip.amazonaws.com");
                BufferedReader br;
                br = new BufferedReader(new InputStreamReader(reqip.openStream()));
                Communicate.node.add(br.readLine());
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

            if (Communicate.reqHandshaking(socket, "nodelist").equals("OK")) {
                Object recv = Communicate.recvSomething(socket);

                if (recv instanceof ArrayList &&
                ((ArrayList)recv).get(0) instanceof String) {
                    Communicate.node = (ArrayList<String>)recv;
                }
            }
            socket.close();
        }

        catch (IOException e) {e.printStackTrace();}
        return 0;
    }
}

package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketReader extends Thread {
    Socket socket;
    String buffer;
    BufferedReader br;
    public SocketReader(Socket newsock) {
        socket = newsock;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public boolean equals(String target) {
        boolean ret;
        synchronized(buffer) {
            if (buffer.startsWith(target)) {
                buffer = buffer.substring(buffer.indexOf(target) + 1);
                ret = true;
            }
            else ret = false;
        }
        return ret;
    }
    public void run() {
        while(true) {
            try {
                buffer += br.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

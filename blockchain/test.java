import java.util.ArrayList;
import java.util.HashMap;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
public class test {
    public static void main(String args[]) {
        try {
            ServerSocket ss = new ServerSocket(111222);
            Socket sock = ss.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            System.out.println(br.readLine());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

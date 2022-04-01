import java.util.ArrayList;
import java.net.Socket;
import java.net.URL;
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
    public static ArrayList<String> asdf = new ArrayList<String>();

    public static void main(String args[]) {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("10.222.112.123", 55555));
        PrintWriter pw = new PrinterWriter(socket.getOutputStream());
        
    }
}

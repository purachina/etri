import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.Serializable;

public class App {
    public static class TC {
        public static String asdf = new String("qwer");
        public static void edit() {
            asdf = "zxcv";
        }
        public static void print() {
            synchronized(asdf) {
            System.out.println(asdf);
            }
        }
    }
    
    public static void main(String[] args) {
        new Thread() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    TC.print();
                    TC.edit();
                }
            }   
        }.start();
        new Thread() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    TC.print();
                    TC.edit();
                }
            }   
        }.start();
        new Thread() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    TC.print();
                    TC.edit();
                }
            }   
        }.start();
    }
}
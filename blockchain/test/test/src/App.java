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
    public static class InnerStruct {
        public String i;
        public InnerStruct() {i = new String("");}
        public void edit(String tmp) {
            synchronized(this.i) {
                i = tmp;
                System.out.println(i);
            }
        }
    }
    public static class TmpStruct {
        public InnerStruct is;
        public TmpStruct() {
            is = new InnerStruct();
        }
        public void edit(String tmp) {synchris.edit(tmp);}
    }
    
    public static void main(String[] args) {
        TmpStruct ts = new TmpStruct();
        new Thread() {
            public void run() {
                for (int i = 0; i < 100; i++) {
                    ts.edit(Integer.toString(i));
                }
            }   
        }.start();
        new Thread() {
            public void run() {
                for (int i = 0; i < 100; i++) {
                    ts.edit(Integer.toString(i));
                }
            }   
        }.start();
        new Thread() {
            public void run() {
                for (int i = 0; i < 100; i++) {
                    ts.edit(Integer.toString(i));
                }
            }   
        }.start();
    }
}
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
    public static class TmpStruct implements Serializable {
        public String qwer;
        public TmpStruct() {
            qwer = "asdf";
        }
    }
    public static void foo(String asdf) {System.out.println(asdf);}
    public static void foo(Object asdf) {System.out.println("object");}
    
    public static void main(String[] args) {
        foo(new TmpStruct());
        foo(new String("String"));
    }
}
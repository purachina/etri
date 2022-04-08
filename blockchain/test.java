import java.util.ArrayList;
import java.util.HashMap;
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
    public static void test(Object o1, Object o2) {
        System.out.println(o1 == o2);
        System.out.println(o1);
        System.out.println(o2);
        ((TO)o1).a = "zxcv";
        System.out.println(o1 + ((TO)o1).a);
        System.out.println(o2 + ((TO)o2).a);
    }
    public static class TO {
        String a;
        public TO() {a = "asdf";}
    }
    public static HashMap<String, String> asdf;
    public static void main(String args[]) {
        TO o1 = new TO();
        test(o1, o1);
        System.out.println(o1.a);
    }
}

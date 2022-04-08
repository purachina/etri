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
    public static HashMap<String, String> asdf;
    public static void main(String args[]) {
        asdf = new HashMap<String, String>();
        asdf.put("asdf", "value1");
        asdf.put("asdf", "value2");
        System.out.println(asdf);
    }
}

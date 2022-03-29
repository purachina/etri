import java.util.ArrayList;

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
        asdf.add("asdf");
        String zxcv = "zxcv";
        try {
            FileOutputStream fos = new FileOutputStream("C:\\test\\binary.abc");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(asdf);
            oos.writeObject(zxcv);
            oos.close();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            FileInputStream fis = new FileInputStream("C:\\test\\binary.abc");
            BufferedInputStream bin = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bin);
            ArrayList<String> qwer = (ArrayList<String>) ois.readObject();
            String uiop = (String)ois.readObject();
            System.out.println(qwer.get(0) + uiop);
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

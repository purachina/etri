package util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import core.Block;
public class BlockChainIO {
    public static boolean fileCheck() {
        File tmpfile = new File("C:\\test\\binary.abc");
        return tmpfile.exists();
    }
    public static ArrayList<Block> readBlockChain() {
        try {
            FileInputStream fis = new FileInputStream("C:\\test\\binary.abc");
            BufferedInputStream bin = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bin);
            ArrayList<Block> ret = (ArrayList<Block>)ois.readObject();
            ois.close();
            fis.close();
            return ret;
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public static int saveBlockChain(ArrayList<Block> target) {
        try {
            FileOutputStream fos = new FileOutputStream("C:\\test\\binary.abc");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(target);
            oos.close();
            fos.close();
            return 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }
}

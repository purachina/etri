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
import java.util.HashMap;

import core.Block;
import core.BlockChain;
public class SerialIO {
    public static boolean fileCheck() {
        File tmpfile = new File("C:\\test\\binary.abc");
        return tmpfile.exists();
    }
    public static int readbin() {
        try {
            FileInputStream fis = new FileInputStream("C:\\test\\binary.abc");
            BufferedInputStream bin = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bin);
            HashMap<String, ArrayList<Block>> blockchainlist = (HashMap<String,ArrayList<Block>>)ois.readObject();
            HashMap<String, ArrayList<String>> nodedict = (HashMap<String, ArrayList<String>>)ois.readObject();
            ois.close();
            fis.close();
            BlockChain.loadBlockChainDict(blockchainlist);
            Communicate.loadNodeDict(nodedict);
            return 0;
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }
    public static int savebin(HashMap<String, ArrayList<Block>> blockchainlist, HashMap<String, ArrayList<String>> nodedict) {
        try {
            FileOutputStream fos = new FileOutputStream("C:\\test\\binary.abc");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(BlockChain.getBCDict());
            oos.writeObject(Communicate.getNodeDict());
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

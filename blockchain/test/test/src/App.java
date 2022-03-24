import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;

public class App {
    public static class TmpStruct {
        public String qwer;
        public TmpStruct() {
            qwer = "asdf";
        }
    }
    
    public static void main(String[] args) {
        TmpStruct asdf = new TmpStruct();
        TmpStruct zxcv = asdf;
        asdf.qwer = "zxcv";
        System.out.println(zxcv.qwer);
    }
}
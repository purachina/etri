package util;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;

public class Hashing {
    
    public static String getHash(Object input){
        StringBuffer res = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            ByteArrayOutputStream byte_out_stream = new ByteArrayOutputStream();
            ObjectOutput obj_out = new ObjectOutputStream(byte_out_stream);
            obj_out.writeObject(input);
            byte[] msg = byte_out_stream.toByteArray();
            md.update(msg);
            byte bytes[] = md.digest();

            for (int i = 0; i < bytes.length; i++){
                res.append(
                    Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}

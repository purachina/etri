package util;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;

public class Hashing {
    
    public static String getHash(String input){
        try {
            StringBuffer res = new StringBuffer();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            ByteArrayOutputStream byte_out_stream = new ByteArrayOutputStream();
            md.update(input.getBytes());
            byte bytes[] = md.digest();

            for (int i = 0; i < bytes.length; i++){
                res.append(
                    Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1)
                );
            }
            return res.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getHash(Object input) {
        try {
            StringBuffer res = new StringBuffer();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            ByteArrayOutputStream byte_out_stream = new ByteArrayOutputStream();
            ObjectOutput obj_out = new ObjectOutputStream(byte_out_stream);
            obj_out.writeObject(input);
            byte[] msg = byte_out_stream.toByteArray();
            md.update(msg);
            byte bytes[] = md.digest();

            for (int i = 0; i < bytes.length; i++) {
                res.append(
                        Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return res.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    return null;
}

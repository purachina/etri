package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;

public class SerialComm {
    protected static String read;
    protected class SerialReader implements Runnable {
        InputStream in;
        public SerialReader(InputStream in) {this.in = in;}
        public void run() {
            byte[] buffer = new byte[1024];
            int len = -1;
            try {
                while((len = in.read(buffer)) > -1) {
                    read += new String(buffer, 0, len);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected class SerialWriter implements Runnable {
        OutputStream out;
        String plain_command;
        public SerialWriter(OutputStream out) {this.out = out;}
        public int command(String plain_command) {
            this.plain_command = plain_command + '\r';
            return 0;
        }
        public void run() {
            while (true) {
                if (!plain_command.equals("")) {
                    try {
                        synchronized(plain_command) {
                        out.write(plain_command.getBytes());
                            plain_command = "";
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

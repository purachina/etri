package util;

import java.net.Socket;
import java.util.ArrayList;
import core.Block;
public class Request {
    protected static ArrayList<Block> reqBlockchain(Socket socket) {
        if (Communicate.reqHandshaking(socket, "blockchain").equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
        }
        return null;
    }
}

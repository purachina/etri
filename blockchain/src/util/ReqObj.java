package util;

import java.net.Socket;
import java.util.ArrayList;
import core.Block;

public class ReqObj {
    protected static ArrayList<Block> reqBlockchain(Socket socket) {
        if (Communicate.reqHandshaking(socket, "blockchain").equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            if (recv instanceof ArrayList) {
                if (((ArrayList)recv).get(0) instanceof Block) {
                    return (ArrayList<Block>)recv;
                }
            }
        }
        return null;
    }
    protected static Block reqBlock(Socket socket) {
        if (Communicate.reqHandshaking(socket, "block").equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            if (recv instanceof Block) return (Block)recv;
        }
        return null;
    }
    protected static ArrayList<String> reqNodeList(Socket socket) {
        if (Communicate.reqHandshaking(socket, "nodelist").equals("OK")) {
            Object recv = Communicate.recvSomething(socket);
            if (recv instanceof ArrayList) {
                if (((ArrayList)recv).get(0) instanceof String) {
                    return (ArrayList<String>)recv;
                }
            }
        }
        return null;
    }
}

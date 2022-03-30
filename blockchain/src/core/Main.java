package core;

import util.Listen;
import util.UserControl;

public class Main {
    public static void main(String args[]) {
        new BlockChain().start();
        new UserControl().start();
        new Listen().start();
    }
}

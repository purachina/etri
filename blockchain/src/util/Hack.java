package util;

import core.BlockChain;

public class Hack {
    public static void hack(String bcid, String bid) {
        BlockChain.blockchaindict.get(bcid).get(Integer.parseInt(bid)).merkletree.txlist.get(0).payer = "Hacked!!!!";
    }
}

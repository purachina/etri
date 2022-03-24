package util;

import java.util.ArrayList;

public class Consensus {
    protected static ArrayList<String> network_hash;
    protected static ArrayList<String> owner_ip;
    public static String hashElection() {
        if (network_hash.size() != Communicate.getNodeList().size()) {
            System.out.println("there's a difference between received stuff and count of node");
            return "";
        }
        ArrayList<String> candidate_list = new ArrayList<String>();
        ArrayList<Integer> vote = new ArrayList<Integer>();
        for (int i = 0; i < network_hash.size(); i++) {
            int idx = candidate_list.indexOf(network_hash.get(i));
            if (idx < 0) {
                candidate_list.add(network_hash.get(i));
                vote.add(1);
            }
            else {
                vote.set(idx, vote.get(idx) + 1);
            }
        }
        int sel = 0;
        for (int i = 0; i < vote.size(); i++) {
            if (vote.get(i).compareTo(network_hash.size() + 1 / 2) > 0) {
                sel = i;
                break;
            }
            if (vote.get(i).compareTo(vote.get(sel)) > 0) sel = i;
        }
        for (int i = 0; i < network_hash.size(); i++) {
            if (!network_hash.get(i).equals(candidate_list.get(sel))) {
                removeCheater(network_hash.get(i));
            }
        }
        return candidate_list.get(sel);
    }
    public static int addHash(String newhash, String owner) {
        synchronized(network_hash) {
            synchronized(owner_ip) {
                network_hash.add(new String(newhash));
                owner_ip.add(new String(owner));
            }
        }
        return 0;
    }
    public static int removeCheater(String cheater) {
        synchronized(network_hash) {
            synchronized(owner_ip) {
                network_hash.remove(owner_ip.indexOf(cheater));
                owner_ip.remove(cheater);
            }
        }
        return 0;
    }
}

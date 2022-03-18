package test;
import java.io.Serializable;
import java.util.ArrayList;

public class test3 implements Serializable {
    private ArrayList<String> tx_list;
    public test3() {
        tx_list = new ArrayList<String>();
        tx_list.add("yes ");
        tx_list.add("i ");
        tx_list.add("can!");
    }
    public void showContents() {
        for (int i = 0; i < tx_list.size(); i++) {
            System.out.print(tx_list.get(i));
        }
    }
    public void setContents(String xcon) {
        tx_list.add(xcon);
    }
}

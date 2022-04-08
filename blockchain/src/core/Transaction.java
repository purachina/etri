package core;

import java.io.Serializable;

import util.Hashing;

public class Transaction implements Serializable{
    
    private String payer, payee, amount;
    
    public Transaction(String newpayer, String newpayee, String newamount) {
        payer = new String(newpayer);
        payee = new String(newpayee);
        amount = new String(newamount);
    }
    public Transaction() {
        payer = new String("0");
        payee = amount = "";
    }
    public Transaction(Transaction tx) {
        payer = new String(tx.getPayer());
        payee = new String(tx.getPayee());
        amount = new String(tx.getAmount());
    }
    public Transaction(String s) {
        payer = new String(s);
        payee = amount = "";
    }
    public Transaction getTX() {
        Transaction ret = new Transaction(this.getPayer(), this.getPayee(), this.getAmount());
        return ret;
    }
    public String getPayer() {return new String(payer);}
    public String getPayee() {return new String(payee);}
    public String getAmount() {return new String(amount);}
    public String getInfo() {
        if (!amount.equals("")) return new String(payer + " sends " + amount + "BTC to " + payee);
        else return payer;
    }
    public int printTX() {
        if (!amount.equals("")) System.out.println(payer + " sends " + amount + "BTC to " + payee);
        else System.out.println(payer);
        return 0;
    }
    public String getHash() {
        String hash = Hashing.makeHash(payer + payee + amount);
        if (hash == null) {
            System.out.println("There's a problem to make hash about tx");
            return null;
        }
        return Hashing.makeHash(payer + payee + amount);
    }
}

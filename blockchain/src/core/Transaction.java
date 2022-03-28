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
        payer = new String("Advanced Secured Decentralized Facility");
        payee = new String("Designed by Purachina");
        amount = new String("0");
    }
    public Transaction getTX() {
        Transaction ret = new Transaction(this.getPayer(), this.getPayee(), this.getAmount());
        return ret;
    }
    public String getPayer() {return new String(payer);}
    public String getPayee() {return new String(payee);}
    public String getAmount() {return new String(amount);}
    public String getInfo() {
        return new String(payer + " sends " + amount + "BTC to " + payee);
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

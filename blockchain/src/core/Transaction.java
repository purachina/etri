package core;

import java.io.Serializable;

import core.Atom.HashString;
import core.Atom.TXString;
import util.Hashing;

public class Transaction implements Serializable{
    
    TXString payer, payee, amount;
    
    public Transaction(String payer, String payee, String amount) {
        this.payer = new TXString();
        this.payee = new TXString();
        this.amount = new TXString();
        this.setPayer(payer);
        this.setPayee(payee);
        this.setAmount(amount);
    }
    public Transaction() {
        this.setPayer("Advanced Secured Decentralized Facility");
        this.setPayee("Designed by Purachina");
        this.setAmount("0");
    }
    public Transaction getTXCopy() {
        Transaction tmptx = new Transaction(this.getPayer(), this.getPayee(), this.getAmount());
        return tmptx;
    }
    public String getPayer() {return this.payer.toString();}
    public String getPayee() {return this.payee.toString();}
    public String getAmount() {return this.amount.toString();}
    public String getInfo() {
        return new String(this.payer.toString() + " sends " + this.amount.toString() + "BTC to " + this.payee.toString());
    }
    public HashString getHash() {
        HashString ret = new HashString(Hashing.getHash(payer.getHash().toString() + payee.getHash().toString() + amount.getHash().toString()));
        return ret;
    }
    protected TXString getPayerOrg() {return this.payer;}
    protected TXString getPayeeOrg() {return this.payee;}
    protected TXString getAmountOrg() {return this.amount;}
    private void setPayer(String payer) {this.payer.content = payer;}
    private void setPayee(String payee) {this.payee.content = payee;}
    private void setAmount(String amount) {this.amount.content = amount;}    
}

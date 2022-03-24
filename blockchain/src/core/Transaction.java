package core;

import java.io.Serializable;
import core.Atom.TXString;

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
    private void setPayer(String payer) {this.payer.txstring = payer;}
    protected String getPayer() {return this.payer.txstring;}
    private void setPayee(String payee) {this.payee.txstring = payee;}
    protected String getPayee() {return this.payee.txstring;}
    private void setAmount(String amount) {this.amount.txstring = amount;}
    protected String getAmount() {return this.amount.txstring;}
    public String getPayerCopy() {
        return new String(this.payer.txstring);
    }
    public String getPayeeCopy() {
        return new String(this.payee.txstring);
    }
    public String getAmountCopy() {
        return new String(this.amount.txstring);
    }
    public String getInfo() {
        return new String(this.payer.txstring + " sends " + this.amount.txstring + "BTC to " + this.payee.txstring);
    }
}

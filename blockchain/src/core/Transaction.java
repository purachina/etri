package core;

import java.io.Serializable;

public class Transaction implements Serializable{
    String payer, payee;
    double amount;
    
    public Transaction(String payer, String payee, double amount) {
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
    }
    public Transaction() {
        this.payer = "Advanced Secured Decentralized Facility";
        this.payee = "Designed by Purachina";
        this.amount = 0.0;
    }
    public void setPayer(String payer) {this.payer = payer;}
    public String getPayer() {return this.payer;}
    public void setPayee(String payee) {this.payee = payee;}
    public String getPayee() {return this.payee;}
    public void setAmount(double amount) {this.amount = amount;}
    public double getAmount() {return this.amount;}
    public String getInfo() {
        return this.payer + " sends " + this.amount + "BTC to " + this.payee;
    }
    public Transaction add(Transaction new_tx) {
        String tmpPayer, tmpPayee;
        double tmpAmount;
        tmpPayer = this.payer + new_tx.getPayer();
        tmpPayee = this.payee + new_tx.getPayee();
        tmpAmount = this.amount + new_tx.getAmount();
        return new Transaction(tmpPayer, tmpPayee, tmpAmount);
    }
}

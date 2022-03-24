package core;

public class Atom {
    
    public static class TXString {
        protected String txstring;
        public TXString() {txstring = new String();}
        public TXString(String tx) {this.txstring = tx;}
        public String getTXCopy() {return new String(txstring);}
    }

    public static class HashString {
        protected String hashvalue;
        public HashString() {this.hashvalue = null;}
        public HashString(String hash) {this.hashvalue = hash;}
        public String getHashCopy() {return new String(hashvalue);}
    }
}

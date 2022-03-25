package core;


import util.Hashing;

public class Atom {
    
    public static class TXString {
        protected String content;
        public TXString() {content = new String();}
        public TXString(String tx) {this.content = tx;}
        public String toString() {return new String(content);}
        protected TXString copy() {return new TXString(new String(this.content));}
        public HashString getHash() {return new HashString(Hashing.getHash(this.content));}
    }

    public static class HashString {
        protected String hashvalue;
        public HashString() {this.hashvalue = null;}
        public HashString(String hash) {this.hashvalue = hash;}
        public HashString(HashString hs) {this.hashvalue = new HashString(hs.toString());}
        public String toString() {return new String(hashvalue);}
        public HashString add(HashString hs) {
            HashString reths = new HashString(this.toString() + hs.toString());
            return reths;
        }
    }
}

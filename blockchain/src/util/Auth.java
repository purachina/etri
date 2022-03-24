package util;

public class Auth {
    public static boolean objObjCompare(Object origin, Object target) {
        if (Hashing.getHash(origin).equals(Hashing.getHash(target))) return true;
        else return false;
    }
    public static boolean hashObjCompare(String hash, Object o) {
        if (Hashing.getHash(o).equals(hash)) return true;
        else return false;
    }
}

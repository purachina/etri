package util;

public class Auth {
    public static boolean hashCompare(Object origin, Object target) {
        if (Hashing.getHash(origin).equals(Hashing.getHash(target))) return true;
        else return false;
    }
}

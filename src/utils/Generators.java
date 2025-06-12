package utils;

public class Generators {
    public static String generateId() {
        String currentTime = String.valueOf(System.currentTimeMillis());
        return "ST" + currentTime;
    }
}

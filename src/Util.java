package src;

/*
Meant to store generally usefully stuff that doesn't fit nicely elsewhere
 */
public class Util {
    public static double true_mod(double a, double b) {
        double remainder = a % b;
        if (remainder < 0) {
            remainder += b;
        }
        return remainder;
    }
}

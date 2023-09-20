package dev.gether.getclan.utils;

public class SystemPoint {

    public static int roundUpToMinutes(long milliseconds) {
        double minutes = (double) milliseconds / (60 * 1000);
        return (int) Math.ceil(minutes);
    }

    public static int calculateEloRating(int oldRating, int opponentRating, double score) {
        int K = 30;
        double expectedScore = calculateExpectedScore(oldRating, opponentRating);
        return oldRating + (int) (K * (score - expectedScore));
    }

    private static double calculateExpectedScore(int playerRating, int opponentRating) {
        return 1.0 / (1 + Math.pow(10, (opponentRating - playerRating) / 400.0));
    }

}

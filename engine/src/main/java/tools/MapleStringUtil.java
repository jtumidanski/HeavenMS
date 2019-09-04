package tools;

public class MapleStringUtil {
   public static String getTimeRemaining(long timeLeft) {
      int seconds = (int) Math.floor(timeLeft / 1000) % 60;
      int minutes = (int) Math.floor(timeLeft / (1000 * 60)) % 60;

      return (minutes > 0 ? (String.format("%02d", minutes) + " minutes, ") : "") + String.format("%02d", seconds) + " seconds";
   }
}

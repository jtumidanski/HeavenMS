package server;

public class MapleTradeUtil {
   public static int getFee(long meso) {
      long fee = 0;
      if (meso >= 100000000) {
         fee = (meso * 6) / 100;
      } else if (meso >= 25000000) {
         fee = (meso * 5) / 100;
      } else if (meso >= 10000000) {
         fee = (meso * 4) / 100;
      } else if (meso >= 5000000) {
         fee = (meso * 3) / 100;
      } else if (meso >= 1000000) {
         fee = (meso * 18) / 1000;
      } else if (meso >= 100000) {
         fee = (meso * 8) / 1000;
      }
      return (int) fee;
   }
}

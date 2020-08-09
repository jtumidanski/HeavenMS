package server.life;

public enum ElementalEffectiveness {
   NORMAL, IMMUNE, STRONG, WEAK, NEUTRAL;

   public static ElementalEffectiveness getByNumber(int num) {
      return switch (num) {
         case 1 -> IMMUNE;
         case 2 -> STRONG;
         case 3 -> WEAK;
         case 4 -> NEUTRAL;
         default -> throw new IllegalArgumentException("Unknown effectiveness: " + num);
      };
   }
}

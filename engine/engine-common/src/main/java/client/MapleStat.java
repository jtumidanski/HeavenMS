package client;

public enum MapleStat {
   SKIN(0x1),
   FACE(0x2),
   HAIR(0x4),
   LEVEL(0x10),
   JOB(0x20),
   STR(0x40),
   DEX(0x80),
   INT(0x100),
   LUK(0x200),
   HP(0x400),
   MAX_HP(0x800),
   MP(0x1000),
   MAX_MP(0x2000),
   AVAILABLE_AP(0x4000),
   AVAILABLE_SP(0x8000),
   EXP(0x10000),
   FAME(0x20000),
   MESO(0x40000),
   PET(0x180008),
   GACHAPON_EXP(0x200000);
   private final int i;

   MapleStat(int i) {
      this.i = i;
   }

   public static MapleStat getByValue(int value) {
      for (MapleStat stat : MapleStat.values()) {
         if (stat.getValue() == value) {
            return stat;
         }
      }
      return null;
   }

   public static MapleStat getBy5ByteEncoding(int encoded) {
      return switch (encoded) {
         case 64 -> STR;
         case 128 -> DEX;
         case 256 -> INT;
         case 512 -> LUK;
         default -> null;
      };
   }

   public static MapleStat getByString(String type) {
      return switch (type) {
         case "SKIN" -> SKIN;
         case "FACE" -> FACE;
         case "HAIR" -> HAIR;
         case "LEVEL" -> LEVEL;
         case "JOB" -> JOB;
         case "STR" -> STR;
         case "DEX" -> DEX;
         case "INT" -> INT;
         case "LUK" -> LUK;
         case "HP" -> HP;
         case "MAXHP" -> MAX_HP;
         case "MP" -> MP;
         case "MAXMP" -> MAX_MP;
         case "AVAILABLEAP" -> AVAILABLE_AP;
         case "AVAILABLESP" -> AVAILABLE_SP;
         case "EXP" -> EXP;
         case "FAME" -> FAME;
         case "MESO" -> MESO;
         case "PET" -> PET;
         default -> null;
      };
   }

   public int getValue() {
      return i;
   }
}

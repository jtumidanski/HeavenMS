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
      switch (encoded) {
         case 64:
            return STR;
         case 128:
            return DEX;
         case 256:
            return INT;
         case 512:
            return LUK;
      }
      return null;
   }

   public static MapleStat getByString(String type) {
      switch (type) {
         case "SKIN":
            return SKIN;
         case "FACE":
            return FACE;
         case "HAIR":
            return HAIR;
         case "LEVEL":
            return LEVEL;
         case "JOB":
            return JOB;
         case "STR":
            return STR;
         case "DEX":
            return DEX;
         case "INT":
            return INT;
         case "LUK":
            return LUK;
         case "HP":
            return HP;
         case "MAXHP":
            return MAX_HP;
         case "MP":
            return MP;
         case "MAXMP":
            return MAX_MP;
         case "AVAILABLEAP":
            return AVAILABLE_AP;
         case "AVAILABLESP":
            return AVAILABLE_SP;
         case "EXP":
            return EXP;
         case "FAME":
            return FAME;
         case "MESO":
            return MESO;
         case "PET":
            return PET;
      }
      return null;
   }

   public int getValue() {
      return i;
   }
}

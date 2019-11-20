package tools;

public enum ServerNoticeType {
   NOTICE(0),
   POP_UP(1),
   MEGAPHONE(2),
   SUPER_MEGAPHONE(3),
   SCROLL(4),
   PINK_TEXT(5),
   LIGHT_BLUE(6);

   private final int value;

   ServerNoticeType(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }

   public static ServerNoticeType get(int i) {
      switch (i) {
         case 0:
            return NOTICE;
         case 1:
            return POP_UP;
         case 2:
            return MEGAPHONE;
         case 3:
            return SUPER_MEGAPHONE;
         case 4:
            return SCROLL;
         case 5:
            return PINK_TEXT;
         case 6:
            return LIGHT_BLUE;
         default:
            throw new UnsupportedOperationException();
      }
   }
}
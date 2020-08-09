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
      return switch (i) {
         case 0 -> NOTICE;
         case 1 -> POP_UP;
         case 2 -> MEGAPHONE;
         case 3 -> SUPER_MEGAPHONE;
         case 4 -> SCROLL;
         case 5 -> PINK_TEXT;
         case 6 -> LIGHT_BLUE;
         default -> throw new UnsupportedOperationException();
      };
   }
}
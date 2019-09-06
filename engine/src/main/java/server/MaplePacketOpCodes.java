package server;

public final class MaplePacketOpCodes {
   public enum VegaScroll {
      //opcodes 0x42, 0x44: "this item cannot be used"; 0x39, 0x45: crashes
      FORTY(0x40),
      FORTY_ONE(0x41),
      FORTY_THREE(0x43);

      private int value;

      VegaScroll(int value) {
         this.value = value;
      }

      public int getValue() {
         return value;
      }
   }
}

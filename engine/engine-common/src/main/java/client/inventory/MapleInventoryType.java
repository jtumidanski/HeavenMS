package client.inventory;

public enum MapleInventoryType {
   UNDEFINED(0),
   EQUIP(1),
   USE(2),
   SETUP(3),
   ETC(4),
   CASH(5),
   CAN_HOLD(6),   //Proof-guard for inserting after removal checks
   EQUIPPED(-1); //Seems nexon screwed something when removing an item T_T
   final byte type;

   MapleInventoryType(int type) {
      this.type = (byte) type;
   }

   public static MapleInventoryType getByType(byte type) {
      for (MapleInventoryType l : MapleInventoryType.values()) {
         if (l.getType() == type) {
            return l;
         }
      }
      return null;
   }

   public static MapleInventoryType getByWZName(String name) {
      return switch (name) {
         case "Install" -> SETUP;
         case "Consume" -> USE;
         case "Etc" -> ETC;
         case "Cash", "Pet" -> CASH;
         default -> UNDEFINED;
      };
   }

   public byte getType() {
      return type;
   }

   public short getBitfieldEncoding() {
      return (short) (2 << type);
   }
}

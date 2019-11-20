package tools.packet.pin;

public enum PinOperation {
   ACCEPTED(0), //PIN was accepted
   NEW_PIN(1), //Register a new PIN
   INVALID(2), //Invalid pin / Reenter
   CONNECTION_FAILED(3), //Connection failed due to system error
   ENTER_PIN(4); //Enter the pin

   private final byte value;

   PinOperation(int value) {
      this.value = (byte) value;
   }

   public byte getValue() {
      return value;
   }

   public static PinOperation fromValue(byte value) {
      for (PinOperation op : PinOperation.values()) {
         if (op.getValue() == value) {
            return op;
         }
      }
      return null;
   }
}

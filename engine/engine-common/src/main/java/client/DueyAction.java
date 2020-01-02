package client;

public enum DueyAction {
   TO_SERVER_RECV_ITEM(0x00),
   TO_SERVER_SEND_ITEM(0x02),
   TO_SERVER_CLAIM_PACKAGE(0x04),
   TO_SERVER_REMOVE_PACKAGE(0x05),
   TO_SERVER_CLOSE_DUEY(0x07),
   TO_CLIENT_OPEN_DUEY(0x08),
   TO_CLIENT_SEND_ENABLE_ACTIONS(0x09),
   TO_CLIENT_SEND_NOT_ENOUGH_MESOS(0x0A),
   TO_CLIENT_SEND_INCORRECT_REQUEST(0x0B),
   TO_CLIENT_SEND_NAME_DOES_NOT_EXIST(0x0C),
   TO_CLIENT_SEND_SAME_ACC_ERROR(0x0D),
   TO_CLIENT_SEND_RECEIVER_STORAGE_FULL(0x0E),
   TO_CLIENT_SEND_RECEIVER_UNABLE_TO_RECV(0x0F),
   TO_CLIENT_SEND_RECEIVER_STORAGE_WITH_UNIQUE(0x10),
   TO_CLIENT_SEND_MESO_LIMIT(0x11),
   TO_CLIENT_SEND_SUCCESSFULLY_SENT(0x12),
   TO_CLIENT_RECV_UNKNOWN_ERROR(0x13),
   TO_CLIENT_RECV_ENABLE_ACTIONS(0x14),
   TO_CLIENT_RECV_NO_FREE_SLOTS(0x15),
   TO_CLIENT_RECV_RECEIVER_WITH_UNIQUE(0x16),
   TO_CLIENT_RECV_SUCCESSFUL_MSG(0x17),
   SOMETHING(0x1A),
   TO_CLIENT_RECV_PACKAGE_MSG(0x1B);
   private final int value;

   DueyAction(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }

   public static DueyAction fromValue(int value) {
      for (DueyAction op : DueyAction.values()) {
         if (op.getValue() == value) {
            return op;
         }
      }
      return null;
   }
}

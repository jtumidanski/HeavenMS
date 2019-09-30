package tools.packet;

public enum MiniRoomError {
   ROOM_ALREADY_CLOSED(1),
   FULL_CAPACITY(2),
   OTHER_REQUESTS(3),
   CANT_WHILE_DEAD(4),
   CANT_IN_EVENT(5),
   UNABLE_TO_WITH_CHARACTER(6),
   NOT_ALLOWED_TO_TRADE(7),
   SAME_MAP_ONLY(9),
   NOT_NEAR_PORTAL(10),
   CANT_START_GAME_HERE(11),
   NOT_IN_CHANNEL(12),
   CANT_ESTABLISH_MINI_ROOM(13),
   CANT_START_GAME_HERE_2(14),
   STORES_ONLY_IN_FM(15),
   LISTS_ROOM_AT_FM(16),
   CANNOT_ENTER(17),
   UNDERGOING_MAINTENANCE(18),
   UNABLE_TO_ENTER_TOURNAMENT_ROOM(19),
   NOT_ALLOWED_TO_TRADE_2(20),
   NOT_ENOUGH_MESOS(21),
   INCORRECT_PASSWORD(22);

   private final int value;

   MiniRoomError(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }

   public static MiniRoomError fromValue(int value) {
      for (MiniRoomError op : MiniRoomError.values()) {
         if (op.getValue() == value) {
            return op;
         }
      }
      return null;
   }
}

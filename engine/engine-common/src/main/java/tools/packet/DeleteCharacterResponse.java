package tools.packet;

public enum DeleteCharacterResponse {
   SUCCESS(0X00), //success
   TROUBLE_LOGGING_IN(0X06), //Trouble logging into the game?
   UNKNOWN_ERROR(0X09), //Unknown error
   TOO_MANY_REQUESTS(0X0A), //Could not be processed due to too many connection requests to the server.
   INVALID_BIRTHDAY(0X12), //invalid bday
   INCORRECT_PIC(0X14), //incorrect pic
   CANNOT_DELETE_GUILD_MASTER(0X16), //Cannot delete a guild master.
   CANNOT_DELETE_PENDING_WEDDING(0X18), //Cannot delete a character with a pending wedding.
   CANNOT_DELETE_PENDING_WORLD_TRANSFER(0X1A), //Cannot delete a character with a pending world transfer.
   CANNOT_DELETE_WITH_FAMILY(0X1D); //Cannot delete a character that has a family.

   private final int value;

   DeleteCharacterResponse(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }

   public static DeleteCharacterResponse fromValue(int value) {
      for (DeleteCharacterResponse op : DeleteCharacterResponse.values()) {
         if (op.getValue() == value) {
            return op;
         }
      }
      return null;
   }
}

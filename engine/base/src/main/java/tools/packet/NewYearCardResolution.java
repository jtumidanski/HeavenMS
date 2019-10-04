package tools.packet;

import client.MapleCharacter;
import client.newyear.NewYearCardRecord;
import net.opcodes.SendOpcode;

public class NewYearCardResolution implements PacketInput {
   private MapleCharacter character;

   private NewYearCardRecord newYearCardRecord;

   private int mode;

   private int message;

   public NewYearCardResolution(MapleCharacter character, NewYearCardRecord newYearCardRecord, int mode, int message) {
      this.character = character;
      this.newYearCardRecord = newYearCardRecord;
      this.mode = mode;
      this.message = message;
   }

   public NewYearCardResolution(MapleCharacter character, int cardId, int mode, int message) {
      this.character = character;
      this.newYearCardRecord = character.getNewYearRecord(cardId);
      this.mode = mode;
      this.message = message;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.NEW_YEAR_CARD_RES;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public NewYearCardRecord getNewYearCardRecord() {
      return newYearCardRecord;
   }

   public int getMode() {
      return mode;
   }

   public int getMessage() {
      return message;
   }
}

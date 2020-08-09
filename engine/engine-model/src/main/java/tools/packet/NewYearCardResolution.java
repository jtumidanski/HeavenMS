package tools.packet;

import client.newyear.NewYearCardRecord;
import net.opcodes.SendOpcode;

public record NewYearCardResolution(Integer characterId, NewYearCardRecord newYearCardRecord, Integer mode,
                                    Integer message) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NEW_YEAR_CARD_RES;
   }
}
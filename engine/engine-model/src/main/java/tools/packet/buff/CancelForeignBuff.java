package tools.packet.buff;

import java.util.List;

import client.MapleBuffStat;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CancelForeignBuff(Integer characterId, List<MapleBuffStat> statIncreases) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CANCEL_FOREIGN_BUFF;
   }
}

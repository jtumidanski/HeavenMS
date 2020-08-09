package tools.packet.buff;

import java.util.List;

import client.MapleBuffStat;
import net.opcodes.SendOpcode;
import tools.Pair;
import tools.packet.PacketInput;

public record GivePirateBuff(List<Pair<MapleBuffStat, Integer>> statIncreases, Integer buffId,
                             Integer duration) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GIVE_BUFF;
   }
}

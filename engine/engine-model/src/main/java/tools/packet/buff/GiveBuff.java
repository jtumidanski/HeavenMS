package tools.packet.buff;

import java.util.List;

import client.MapleBuffStat;
import net.opcodes.SendOpcode;
import tools.Pair;
import tools.packet.PacketInput;

public record GiveBuff(Integer buffId, Integer buffLength,
                       List<Pair<MapleBuffStat, Integer>> statIncreases) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GIVE_BUFF;
   }
}

package tools.packet.buff;

import java.util.List;

import client.MapleAbnormalStatus;
import net.opcodes.SendOpcode;
import server.life.MobSkill;
import tools.Pair;
import tools.packet.PacketInput;

public record GiveForeignAbnormalStatus(Integer characterId, List<Pair<MapleAbnormalStatus, Integer>> statIncreases,
                                        MobSkill mobSkill) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GIVE_FOREIGN_BUFF;
   }
}

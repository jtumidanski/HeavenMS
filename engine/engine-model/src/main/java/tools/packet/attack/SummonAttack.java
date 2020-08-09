package tools.packet.attack;

import java.util.List;

import net.opcodes.SendOpcode;
import net.server.channel.handlers.SummonAttackEntry;
import tools.packet.PacketInput;

public record SummonAttack(Integer characterId, Integer summonObjectId, Byte direction,
                           List<SummonAttackEntry> damage) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SUMMON_ATTACK;
   }
}

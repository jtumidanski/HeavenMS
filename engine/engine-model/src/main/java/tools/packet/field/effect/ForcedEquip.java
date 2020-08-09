package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ForcedEquip(int team) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FORCED_MAP_EQUIP;
   }
}
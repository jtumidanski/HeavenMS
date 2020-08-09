package tools.packet.fredrick;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetFredrickInfo(Integer characterId, Integer merchantNetMeso) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FREDRICK;
   }
}
package tools.packet.field;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CrimsonBalrogBoat(Boolean theType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CONTI_MOVE;
   }
}
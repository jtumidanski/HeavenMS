package tools.packet.family;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record FamilyMessage(Integer theType, Integer mesos) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_RESULT;
   }
}
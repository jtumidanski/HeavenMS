package tools.packet.family;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SeniorMessage(String characterName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_JOIN_ACCEPTED;
   }
}
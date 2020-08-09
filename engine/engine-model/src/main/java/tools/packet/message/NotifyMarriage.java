package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record NotifyMarriage(Integer theType, String characterName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NOTIFY_MARRIAGE;
   }
}
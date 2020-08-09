package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record NotifyLevelUp(Integer theType, Integer level, String characterName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NOTIFY_LEVEL_UP;
   }
}
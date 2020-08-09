package tools.packet.alliance;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record AllianceNotice(Integer id, String notice) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ALLIANCE_OPERATION;
   }
}

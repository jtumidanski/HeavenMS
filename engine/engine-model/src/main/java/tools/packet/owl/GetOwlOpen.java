package tools.packet.owl;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetOwlOpen(List<Integer> leaderboards) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOP_SCANNER_RESULT;
   }
}
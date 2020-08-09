package tools.packet.ui;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RefreshTeleportRockMapList(List<Integer> vips, List<Integer> regulars, Boolean delete,
                                         Boolean vip) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MAP_TRANSFER_RESULT;
   }
}
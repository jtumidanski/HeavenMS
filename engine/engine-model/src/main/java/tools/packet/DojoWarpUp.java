package tools.packet;

import net.opcodes.SendOpcode;

public record DojoWarpUp() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DOJO_WARP_UP;
   }
}
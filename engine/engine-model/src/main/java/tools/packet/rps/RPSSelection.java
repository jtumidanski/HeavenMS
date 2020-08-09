package tools.packet.rps;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RPSSelection(Byte selection, Byte answer) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.RPS_GAME;
   }
}
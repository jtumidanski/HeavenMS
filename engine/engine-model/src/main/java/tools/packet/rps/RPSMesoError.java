package tools.packet.rps;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RPSMesoError(Integer mesos) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.RPS_GAME;
   }
}
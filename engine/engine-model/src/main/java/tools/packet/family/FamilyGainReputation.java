package tools.packet.family;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record FamilyGainReputation(Integer gain, String characterNameFrom) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_REP_GAIN;
   }
}
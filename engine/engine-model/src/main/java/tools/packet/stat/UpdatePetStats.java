package tools.packet.stat;

import client.inventory.MaplePet;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdatePetStats(MaplePet[] pets) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.STAT_CHANGED;
   }
}
package tools.packet.character;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateMount(Integer characterId, Integer mountLevel, Integer mountExp, Integer mountTiredness,
                          Boolean levelUp) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SET_TAMING_MOB_INFO;
   }
}
package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ChangeBackgroundEffect(boolean remove, int layer, int transition) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SET_BACK_EFFECT;
   }
}
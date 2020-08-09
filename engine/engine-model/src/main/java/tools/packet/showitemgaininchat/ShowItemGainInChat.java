package tools.packet.showitemgaininchat;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowItemGainInChat(Integer itemId, Short quantity) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_ITEM_GAIN_IN_CHAT;
   }
}
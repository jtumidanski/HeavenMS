package tools.packet.shop;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoteChannelChange(Byte channelId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ENTRUSTED_SHOP_CHECK_RESULT;
   }
}
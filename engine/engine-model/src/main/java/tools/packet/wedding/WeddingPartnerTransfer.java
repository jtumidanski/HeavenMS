package tools.packet.wedding;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record WeddingPartnerTransfer(Integer partnerId, Integer mapId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NOTIFY_MARRIED_PARTNER_MAP_TRANSFER;
   }
}
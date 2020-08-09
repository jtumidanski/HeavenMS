package tools.packet.item.enhance;

import net.opcodes.SendOpcode;
import server.MaplePacketOpCodes;
import tools.packet.PacketInput;

public record SendVegaScroll(MaplePacketOpCodes.VegaScroll operation) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.VEGA_SCROLL;
   }
}
package tools.packet.mtsoperation;

import java.util.List;

import net.opcodes.SendOpcode;
import server.MTSItemInfo;
import tools.packet.PacketInput;

public record GetNotYetSoldMTSInventory(List<MTSItemInfo> items) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MTS_OPERATION;
   }
}
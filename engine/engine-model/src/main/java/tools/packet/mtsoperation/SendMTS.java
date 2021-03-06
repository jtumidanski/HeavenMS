package tools.packet.mtsoperation;

import java.util.List;

import net.opcodes.SendOpcode;
import server.MTSItemInfo;
import tools.packet.PacketInput;

public record SendMTS(List<MTSItemInfo> items, Integer tab, Integer theType, Integer page,
                      Integer pages) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MTS_OPERATION;
   }
}
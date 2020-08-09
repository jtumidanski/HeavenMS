package tools.packet.guild.bbs;

import java.util.List;

import client.database.data.BbsThreadData;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetThreadList(List<BbsThreadData> threadData, Integer start) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_BBS_PACKET;
   }
}
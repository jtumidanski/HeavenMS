package tools.packet.guild.bbs;

import client.database.data.BbsThreadData;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowThread(Integer localThreadId, BbsThreadData threadData) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_BBS_PACKET;
   }
}
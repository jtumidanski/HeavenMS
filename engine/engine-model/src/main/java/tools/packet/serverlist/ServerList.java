package tools.packet.serverlist;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.ChannelLoad;
import tools.packet.PacketInput;

public record ServerList(Integer serverId, String serverName, Integer flag, String eventMsg,
                         List<ChannelLoad> channelLoad) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SERVER_LIST;
   }
}
package tools.packet;

import client.MapleClient;
import net.opcodes.SendOpcode;

public class CharacterList implements PacketInput {
   private MapleClient client;

   private int serverId;

   private int status;

   public CharacterList(MapleClient client, int serverId, int status) {
      this.client = client;
      this.serverId = serverId;
      this.status = status;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.CHARLIST;
   }

   public MapleClient getClient() {
      return client;
   }

   public int getServerId() {
      return serverId;
   }

   public int getStatus() {
      return status;
   }
}

package tools.packet.character;

import client.MapleClient;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

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
      return SendOpcode.CHARACTER_LIST;
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

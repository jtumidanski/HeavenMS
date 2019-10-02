package tools.packet;

import client.MapleClient;
import net.opcodes.SendOpcode;

public class SetITC implements PacketInput {
   private MapleClient client;

   public SetITC(MapleClient client) {
      this.client = client;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SET_ITC;
   }

   public MapleClient getClient() {
      return client;
   }
}

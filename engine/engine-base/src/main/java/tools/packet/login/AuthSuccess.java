package tools.packet.login;

import client.MapleClient;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class AuthSuccess implements PacketInput {
   private final MapleClient client;

   public AuthSuccess(MapleClient client) {
      this.client = client;
   }

   public MapleClient getClient() {
      return client;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.LOGIN_STATUS;
   }
}

package tools.packet;

import client.MapleClient;
import net.opcodes.SendOpcode;

public class SetCashShop implements PacketInput {
   private final MapleClient client;

   public SetCashShop(MapleClient client) {
      this.client = client;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SET_CASH_SHOP;
   }

   public MapleClient getClient() {
      return client;
   }
}

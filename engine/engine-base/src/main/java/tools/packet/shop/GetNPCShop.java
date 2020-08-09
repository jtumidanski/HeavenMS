package tools.packet.shop;

import java.util.List;

import client.MapleClient;
import net.opcodes.SendOpcode;
import server.MapleShopItem;
import tools.packet.PacketInput;

public class GetNPCShop implements PacketInput {
   private final MapleClient client;

   private final int shopId;

   private final List<MapleShopItem> shopItems;

   public GetNPCShop(MapleClient client, int shopId, List<MapleShopItem> shopItems) {
      this.client = client;
      this.shopId = shopId;
      this.shopItems = shopItems;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.OPEN_NPC_SHOP;
   }

   public MapleClient getClient() {
      return client;
   }

   public int getShopId() {
      return shopId;
   }

   public List<MapleShopItem> getShopItems() {
      return shopItems;
   }
}

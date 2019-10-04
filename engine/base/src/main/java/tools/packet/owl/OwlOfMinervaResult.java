package tools.packet.owl;

import java.util.List;

import net.opcodes.SendOpcode;
import server.maps.AbstractMapleMapObject;
import server.maps.MaplePlayerShopItem;
import tools.Pair;
import tools.packet.PacketInput;

public class OwlOfMinervaResult implements PacketInput {
   private int itemId;

   private List<Pair<MaplePlayerShopItem, AbstractMapleMapObject>> hmsAvailable;

   public OwlOfMinervaResult(int itemId, List<Pair<MaplePlayerShopItem, AbstractMapleMapObject>> hmsAvailable) {
      this.itemId = itemId;
      this.hmsAvailable = hmsAvailable;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOP_SCANNER_RESULT;
   }

   public int getItemId() {
      return itemId;
   }

   public List<Pair<MaplePlayerShopItem, AbstractMapleMapObject>> getHmsAvailable() {
      return hmsAvailable;
   }
}

package tools.packet.item.drop;

import net.opcodes.SendOpcode;
import server.maps.MapleMapItem;
import tools.packet.PacketInput;

public class UpdateMapItemObject implements PacketInput {
   private MapleMapItem mapItem;

   private boolean giveOwnership;

   public UpdateMapItemObject(MapleMapItem mapItem, boolean giveOwnership) {
      this.mapItem = mapItem;
      this.giveOwnership = giveOwnership;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.DROP_ITEM_FROM_MAPOBJECT;
   }

   public MapleMapItem getMapItem() {
      return mapItem;
   }

   public boolean isGiveOwnership() {
      return giveOwnership;
   }
}

package tools.packet.item.drop;

import java.awt.Point;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import server.maps.MapleMapItem;
import tools.packet.PacketInput;

public class DropItemFromMapObject implements PacketInput {
   private final MapleCharacter character;

   private final MapleMapItem mapItem;

   private final Point dropFrom;

   private final Point dropTo;

   private final byte mod;

   public DropItemFromMapObject(MapleCharacter character, MapleMapItem mapItem, Point dropFrom, Point dropTo, byte mod) {
      this.character = character;
      this.mapItem = mapItem;
      this.dropFrom = dropFrom;
      this.dropTo = dropTo;
      this.mod = mod;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.DROP_ITEM_FROM_MAP_OBJECT;
   }

   public MapleCharacter getCharacter() {
      return character;
   }

   public MapleMapItem getMapItem() {
      return mapItem;
   }

   public Point getDropFrom() {
      return dropFrom;
   }

   public Point getDropTo() {
      return dropTo;
   }

   public byte getMod() {
      return mod;
   }
}

package server.maps;

import java.awt.Point;

public class MapleKite extends AbstractMapleMapObject {
   private final String ownerName;

   private final Point pos;

   private final Integer ft;

   private final String text;

   private final Integer itemId;

   public MapleKite(String ownerName, Point pos, Integer ft, String text, Integer itemId) {
      this.ownerName = ownerName;
      this.pos = pos;
      this.ft = ft;
      this.text = text;
      this.itemId = itemId;
   }

   public String ownerName() {
      return ownerName;
   }

   public Point pos() {
      return pos;
   }

   public Integer ft() {
      return ft;
   }

   public String text() {
      return text;
   }

   public Integer itemId() {
      return itemId;
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.KITE;
   }

   @Override
   public Point position() {
      return pos.getLocation();
   }

   @Override
   public void setPosition(Point position) {
      throw new UnsupportedOperationException();
   }
}

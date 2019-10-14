package server.maps;

import java.awt.Point;

import client.MapleCharacter;

public class MapleKite extends AbstractMapleMapObject {

   private Point pos;
   private MapleCharacter owner;
   private String text;
   private int ft;
   private int itemId;

   public MapleKite(MapleCharacter owner, String text, int itemId) {
      this.owner = owner;
      this.pos = owner.getPosition();
      this.ft = owner.getFh();
      this.text = text;
      this.itemId = itemId;
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.KITE;
   }

   @Override
   public Point getPosition() {
      return pos.getLocation();
   }

   @Override
   public void setPosition(Point position) {
      throw new UnsupportedOperationException();
   }

   public MapleCharacter getOwner() {
      return owner;
   }

   public Point getPos() {
      return pos;
   }

   public String getText() {
      return text;
   }

   public int getFt() {
      return ft;
   }

   public int getItemId() {
      return itemId;
   }
}
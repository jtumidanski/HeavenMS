package server.maps;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import tools.PacketCreator;
import tools.packet.remove.RemoveKite;
import tools.packet.spawn.SpawnKite;

public class MapleKite extends AbstractMapleMapObject {

   private Point pos;
   private MapleCharacter owner;
   private String text;
   private int ft;
   private int itemid;

   public MapleKite(MapleCharacter owner, String text, int itemid) {
      this.owner = owner;
      this.pos = owner.getPosition();
      this.ft = owner.getFh();
      this.text = text;
      this.itemid = itemid;
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

   @Override
   public void sendDestroyData(MapleClient client) {
      client.announce(makeDestroyData());
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      client.announce(makeSpawnData());
   }

   public final byte[] makeSpawnData() {
      return PacketCreator.create(new SpawnKite(getObjectId(), itemid, owner.getName(), text, pos, ft));
   }

   public final byte[] makeDestroyData() {
      return PacketCreator.create(new RemoveKite(getObjectId(), 0));
   }
}
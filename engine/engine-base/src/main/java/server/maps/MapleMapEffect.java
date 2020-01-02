package server.maps;

import client.MapleClient;
import tools.PacketCreator;
import tools.packet.field.effect.BlowWeather;
import tools.packet.field.effect.RemoveWeather;

public class MapleMapEffect {
   private String msg;
   private int itemId;
   private boolean active = true;

   public MapleMapEffect(String msg, int itemId) {
      this.msg = msg;
      this.itemId = itemId;
   }

   public final byte[] makeDestroyData() {
      return PacketCreator.create(new RemoveWeather());
   }

   public final byte[] makeStartData() {
      return PacketCreator.create(new BlowWeather(msg, itemId, active));
   }

   public void sendStartData(MapleClient client) {
      client.announce(makeStartData());
   }
}

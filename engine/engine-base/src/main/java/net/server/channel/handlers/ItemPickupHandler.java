package net.server.channel.handlers;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ItemPickupPacket;
import net.server.channel.packet.reader.ItemPickupReader;
import server.maps.MapleMapObject;

public final class ItemPickupHandler extends AbstractPacketHandler<ItemPickupPacket> {
   @Override
   public Class<ItemPickupReader> getReaderClass() {
      return ItemPickupReader.class;
   }

   @Override
   public void handlePacket(ItemPickupPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleMapObject ob = chr.getMap().getMapObject(packet.objectId());
      if (ob == null) {
         return;
      }

      Point charPos = chr.position();
      Point obPos = ob.position();
      if (Math.abs(charPos.getX() - obPos.getX()) > 800 || Math.abs(charPos.getY() - obPos.getY()) > 600) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, client.getPlayer().getName() + " tried to pick up an item too far away. Map id: " + chr.getMapId() + " Player pos: " + charPos + " Object pos: " + obPos);
         return;
      }

      chr.pickupItem(ob);
   }
}

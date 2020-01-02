package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.AddTeleportRockMapPacket;
import net.server.channel.packet.BaseTeleportRockMapPacket;
import net.server.channel.packet.DeleteTeleportRockMapPacket;
import net.server.channel.packet.reader.TeleportRockMapReader;
import server.maps.FieldLimit;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.ui.RefreshTeleportRockMapList;

public final class TeleportRockAddMapHandler extends AbstractPacketHandler<BaseTeleportRockMapPacket> {
   @Override
   public Class<TeleportRockMapReader> getReaderClass() {
      return TeleportRockMapReader.class;
   }

   @Override
   public void handlePacket(BaseTeleportRockMapPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (packet instanceof DeleteTeleportRockMapPacket) {
         if (packet.vip()) {
            chr.deleteVipTeleportRockMap(((DeleteTeleportRockMapPacket) packet).mapId());
         } else {
            chr.deleteTeleportRockMap(((DeleteTeleportRockMapPacket) packet).mapId());
         }
         PacketCreator.announce(client, new RefreshTeleportRockMapList(chr.getVipTeleportRockMaps(), chr.getTeleportRockMaps(), true, packet.vip()));
      } else if (packet instanceof AddTeleportRockMapPacket) {
         if (!FieldLimit.CANNOT_VIP_ROCK.check(chr.getMap().getFieldLimit())) {
            if (packet.vip()) {
               chr.addVipTeleportRockMap();
            } else {
               chr.addTeleportRockMap();
            }

            PacketCreator.announce(client, new RefreshTeleportRockMapList(chr.getVipTeleportRockMaps(), chr.getTeleportRockMaps(), false, packet.vip()));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "You may not save this map.");
         }
      }
   }
}

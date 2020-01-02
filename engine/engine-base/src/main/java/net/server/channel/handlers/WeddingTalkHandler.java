package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.WeddingTalkReader;
import net.server.channel.packet.wedding.BaseWeddingTalkPacket;
import scripting.event.EventInstanceManager;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;
import tools.packet.wedding.WeddingEnd;
import tools.packet.wedding.WeddingProgress;

public final class WeddingTalkHandler extends AbstractPacketHandler<BaseWeddingTalkPacket> {
   @Override
   public Class<WeddingTalkReader> getReaderClass() {
      return WeddingTalkReader.class;
   }

   @Override
   public void handlePacket(BaseWeddingTalkPacket packet, MapleClient client) {
      if (packet.action() == 1) {
         EventInstanceManager eim = client.getPlayer().getEventInstance();

         if (eim != null && !(client.getPlayer().getId() == eim.getIntProperty("groomId") || client.getPlayer().getId() == eim.getIntProperty("brideId"))) {
            PacketCreator.announce(client, new WeddingProgress(false, 0, 0, (byte) 2));
         } else {
            PacketCreator.announce(client, new WeddingEnd(true, 0, 0, (byte) 3));
         }
      } else {
         PacketCreator.announce(client, new WeddingEnd(true, 0, 0, (byte) 3));
      }

      PacketCreator.announce(client, new EnableActions());
   }
}
package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import scripting.event.EventInstanceManager;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;
import tools.packet.wedding.WeddingEnd;

public final class WeddingTalkMoreHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      EventInstanceManager eim = client.getPlayer().getEventInstance();
      if (eim != null && !(client.getPlayer().getId() == eim.getIntProperty("groomId") || client.getPlayer().getId() == eim.getIntProperty("brideId"))) {
         eim.gridInsert(client.getPlayer(), 1);
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, "High Priest John: Your blessings have been added to their love. What a noble act for a lovely couple!");
      }

      PacketCreator.announce(client, new WeddingEnd(true, 0, 0, (byte) 3));
      PacketCreator.announce(client, new EnableActions());
   }
}
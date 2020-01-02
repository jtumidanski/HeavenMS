package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.maps.MapleHiredMerchant;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.shop.RemoteChannelChange;
import tools.packet.stat.EnableActions;

public class RemoteStoreHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleHiredMerchant hm = getMerchant(client);
      if (hm != null && hm.isOwner(chr)) {
         if (hm.getChannel() == chr.getClient().getChannel()) {
            hm.visitShop(chr);
         } else {
            PacketCreator.announce(client, new RemoteChannelChange((byte) (hm.getChannel() - 1)));
         }
         return;
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You don't have a Merchant open.");
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private MapleHiredMerchant getMerchant(MapleClient c) {
      if (c.getPlayer().hasMerchant()) {
         return c.getWorldServer().getHiredMerchant(c.getPlayer().getId());
      }
      return null;
   }
}

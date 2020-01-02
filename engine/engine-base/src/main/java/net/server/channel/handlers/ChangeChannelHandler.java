package net.server.channel.handlers;

import client.MapleClient;
import client.autoban.AutoBanFactory;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.ChangeChannelPacket;
import net.server.channel.packet.reader.ChangeChannelReader;

public final class ChangeChannelHandler extends AbstractPacketHandler<ChangeChannelPacket> {
   @Override
   public Class<ChangeChannelReader> getReaderClass() {
      return ChangeChannelReader.class;
   }

   @Override
   public void handlePacket(ChangeChannelPacket packet, MapleClient client) {
      client.getPlayer().getAutoBanManager().setTimestamp(6, Server.getInstance().getCurrentTimestamp(), 3);
      if (client.getChannel() == packet.channel()) {
         AutoBanFactory.GENERAL.alert(client.getPlayer(), "Changing channel to same channel.");
         client.disconnect(false, false);
         return;
      } else if (client.getPlayer().getCashShop().isOpened() || client.getPlayer().getMiniGame() != null || client.getPlayer().getPlayerShop() != null) {
         return;
      }

      client.changeChannel(packet.channel());
   }
}
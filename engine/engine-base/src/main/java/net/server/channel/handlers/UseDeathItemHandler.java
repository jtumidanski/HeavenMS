package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseDeathItemPacket;
import net.server.channel.packet.reader.UseDeathItemReader;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowItemEffect;

public final class UseDeathItemHandler extends AbstractPacketHandler<UseDeathItemPacket> {
   @Override
   public Class<UseDeathItemReader> getReaderClass() {
      return UseDeathItemReader.class;
   }

   @Override
   public void handlePacket(UseDeathItemPacket packet, MapleClient client) {
      client.getPlayer().setItemEffect(packet.itemId());
      PacketCreator.announce(client, new ShowItemEffect(client.getPlayer().getId(), packet.itemId()));
   }
}

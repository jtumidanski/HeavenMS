package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.CancelItemEffectPacket;
import net.server.channel.packet.reader.CancelItemEffectReader;
import server.MapleItemInformationProvider;

public final class CancelItemEffectHandler extends AbstractPacketHandler<CancelItemEffectPacket> {
   @Override
   public Class<CancelItemEffectReader> getReaderClass() {
      return CancelItemEffectReader.class;
   }

   @Override
   public void handlePacket(CancelItemEffectPacket packet, MapleClient client) {
      if (MapleItemInformationProvider.getInstance().noCancelMouse(packet.itemId())) {
         return;
      }
      client.getPlayer().cancelEffect(packet.itemId());
   }
}
package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ChangeMapSpecialPacket;
import net.server.channel.packet.reader.ChangeMapSpecialReader;
import server.MapleTradeResult;
import server.maps.MaplePortal;
import server.processor.MapleTradeProcessor;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public final class ChangeMapSpecialHandler extends AbstractPacketHandler<ChangeMapSpecialPacket> {
   @Override
   public Class<ChangeMapSpecialReader> getReaderClass() {
      return ChangeMapSpecialReader.class;
   }

   @Override
   public void handlePacket(ChangeMapSpecialPacket packet, MapleClient client) {
      MapleCharacter character = client.getPlayer();
      MaplePortal portal = character.getMap().getPortal(packet.startWarp());
      if (portal == null || character.portalDelay() > currentServerTime() || character.getBlockedPortals().contains(portal.getScriptName())) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }
      if (character.isChangingMaps() || character.isBanned()) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      character.getTrade().ifPresent(trade -> MapleTradeProcessor.getInstance().cancelTrade(character, MapleTradeResult.UNSUCCESSFUL_ANOTHER_MAP));
      portal.enterPortal(client);
   }
}

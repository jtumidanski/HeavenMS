package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.alliance.DenyAllianceRequestPacket;
import net.server.channel.packet.reader.DenyAllianceRequestReader;
import net.server.processor.MapleAllianceProcessor;

public final class DenyAllianceRequestHandler extends AbstractPacketHandler<DenyAllianceRequestPacket> {
   @Override
   public Class<DenyAllianceRequestReader> getReaderClass() {
      return DenyAllianceRequestReader.class;
   }

   @Override
   public void handlePacket(DenyAllianceRequestPacket packet, MapleClient client) {
      client.getWorldServer().getPlayerStorage().getCharacterByName(packet.inviterName())
            .flatMap(MapleCharacter::getAlliance)
            .ifPresent(alliance -> MapleAllianceProcessor.getInstance().answerInvitation(client.getPlayer().getId(), packet.guildName(), alliance.id(), false));
   }
}
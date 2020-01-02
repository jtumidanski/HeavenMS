package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.guild.DenyGuildRequestPacket;
import net.server.channel.packet.reader.DenyGuildRequestReader;
import net.server.processor.MapleGuildProcessor;

public final class DenyGuildRequestHandler extends AbstractPacketHandler<DenyGuildRequestPacket> {
   @Override
   public Class<DenyGuildRequestReader> getReaderClass() {
      return DenyGuildRequestReader.class;
   }

   @Override
   public void handlePacket(DenyGuildRequestPacket packet, MapleClient client) {
      client.getWorldServer().getPlayerStorage().getCharacterByName(packet.characterName())
            .ifPresent(from -> MapleGuildProcessor.getInstance().answerInvitation(client.getPlayer().getId(), client.getPlayer().getName(), from.getGuildId(), false));
   }
}

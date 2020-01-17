package net.server.channel.handlers;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.party.PartySearchStartPacket;
import net.server.channel.packet.reader.PartySearchStartReader;
import net.server.world.MapleParty;
import net.server.world.World;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.stat.EnableActions;

public class PartySearchStartHandler extends AbstractPacketHandler<PartySearchStartPacket> {
   @Override
   public Class<PartySearchStartReader> getReaderClass() {
      return PartySearchStartReader.class;
   }

   @Override
   public void handlePacket(PartySearchStartPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (packet.min() > packet.max()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PARTY_SEARCH_MIN_MAX_ERROR"));
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (packet.max() - packet.min() > 30) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PARTY_SEARCH_MAX_RANGE_ERROR"));
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (chr.getLevel() < packet.min() || chr.getLevel() > packet.max()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PARTY_SEARCH_LEVEL_RANGE_INCLUSION_ERROR"));
         PacketCreator.announce(client, new EnableActions());
         return;
      }


      Optional<MapleParty> party = client.getPlayer().getParty();
      if (party.isEmpty() || !client.getPlayer().isPartyLeader()) {
         return;
      }

      World world = client.getWorldServer();
      world.getPartySearchCoordinator().registerPartyLeader(chr, packet.min(), packet.max(), packet.jobs());
   }
}
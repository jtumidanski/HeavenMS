package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.party.DenyPartyRequestPacket;
import net.server.channel.packet.reader.DenyPartyRequestReader;
import net.server.coordinator.world.MapleInviteCoordinator;
import net.server.coordinator.world.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.world.MapleInviteCoordinator.InviteType;
import scala.Option;
import tools.PacketCreator;
import tools.packet.party.PartyStatusMessage;

public final class DenyPartyRequestHandler extends AbstractPacketHandler<DenyPartyRequestPacket> {
   @Override
   public Class<DenyPartyRequestReader> getReaderClass() {
      return DenyPartyRequestReader.class;
   }

   @Override
   public void handlePacket(DenyPartyRequestPacket packet, MapleClient client) {
      String[] cname = packet.message().split("PS: ");
      client.getChannelServer().getPlayerStorage().getCharacterByName(cname[cname.length - 1]).ifPresent(characterFrom -> {
         MapleCharacter chr = client.getPlayer();
         if (MapleInviteCoordinator.answerInvite(InviteType.PARTY, chr.getId(), characterFrom.getPartyId(), false).result == InviteResult.DENIED) {
            chr.updatePartySearchAvailability(chr.getParty().isEmpty());
            PacketCreator.announce(characterFrom, new PartyStatusMessage(23, Option.apply(chr.getName())));
         }
      });
   }
}

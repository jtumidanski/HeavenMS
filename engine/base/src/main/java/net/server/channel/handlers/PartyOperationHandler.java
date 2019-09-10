/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.server.channel.handlers;

import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.party.BasePartyOperationPacket;
import net.server.channel.packet.party.ChangeLeaderPartyPacket;
import net.server.channel.packet.party.CreatePartyPacket;
import net.server.channel.packet.party.ExpelPartyPacket;
import net.server.channel.packet.party.InvitePartyPacket;
import net.server.channel.packet.party.JoinPartyPacket;
import net.server.channel.packet.party.LeavePartyPacket;
import net.server.channel.packet.reader.PartyOperationReader;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.MapleInviteCoordinator.InviteType;
import net.server.processor.MaplePartyProcessor;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import net.server.world.PartyOperation;
import net.server.world.World;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public final class PartyOperationHandler extends AbstractPacketHandler<BasePartyOperationPacket, PartyOperationReader> {
   @Override
   public Class<PartyOperationReader> getReaderClass() {
      return PartyOperationReader.class;
   }

   @Override
   public void handlePacket(BasePartyOperationPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      World world = client.getWorldServer();
      MapleParty party = player.getParty();

      if (packet instanceof CreatePartyPacket) {
         create(player);
      } else if (packet instanceof LeavePartyPacket) {
         leave(client, player, party);
      } else if (packet instanceof JoinPartyPacket) {
         join(player, ((JoinPartyPacket) packet).partyId());
      } else if (packet instanceof InvitePartyPacket) {
         invite(client, player, world, party, ((InvitePartyPacket) packet).name());
      } else if (packet instanceof ExpelPartyPacket) {
         expel(client, party, ((ExpelPartyPacket) packet).characterId());
      } else if (packet instanceof ChangeLeaderPartyPacket) {
         changeLeader(world, party, ((ChangeLeaderPartyPacket) packet).leaderId());
      }
   }

   private void changeLeader(World world, MapleParty party, int newLeader) {
      MaplePartyCharacter partyCharacter = party.getMemberById(newLeader);
      world.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, partyCharacter);
   }

   private void expel(MapleClient client, MapleParty party, int characterId) {
      MaplePartyProcessor.getInstance().expelFromParty(party, client, characterId);
   }

   private void invite(MapleClient client, MapleCharacter player, World world, MapleParty party, String name) {
      Optional<MapleCharacter> invitedOptional = world.getPlayerStorage().getCharacterByName(name);
      if (invitedOptional.isEmpty()) {
         client.announce(MaplePacketCreator.partyStatusMessage(19));
      } else {
         MapleCharacter invited = invitedOptional.get();
         if (invited.getLevel() < 10 && (!ServerConstants.USE_PARTY_FOR_STARTERS || player.getLevel() >= 10)) { //min requirement is level 10
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "The player you have invited does not meet the requirements.");
            return;
         }
         if (ServerConstants.USE_PARTY_FOR_STARTERS && invited.getLevel() >= 10 && player.getLevel() < 10) {    //trying to invite high level
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "The player you have invited does not meet the requirements.");
            return;
         }

         if (invited.getParty() == null) {
            if (party == null) {
               if (!MaplePartyProcessor.getInstance().createParty(player, false)) {
                  return;
               }

               party = player.getParty();
            }
            if (party.getMembers().size() < 6) {
               if (MapleInviteCoordinator.createInvite(InviteType.PARTY, player, party.getId(), invited.getId())) {
                  invited.getClient().announce(MaplePacketCreator.partyInvite(player));
               } else {
                  client.announce(MaplePacketCreator.partyStatusMessage(22, invited.getName()));
               }
            } else {
               client.announce(MaplePacketCreator.partyStatusMessage(17));
            }
         } else {
            client.announce(MaplePacketCreator.partyStatusMessage(16));
         }
      }
   }

   private void join(MapleCharacter player, int partyId) {
      MapleInviteCoordinator.MapleInviteResult inviteRes = MapleInviteCoordinator.answerInvite(InviteType.PARTY, player.getId(), partyId, true);
      InviteResult res = inviteRes.result;
      if (res == InviteResult.ACCEPTED) {
         MaplePartyProcessor.getInstance().joinParty(player, partyId, false);
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "You couldn't join the party due to an expired invitation request.");
      }
   }

   private void leave(MapleClient client, MapleCharacter player, MapleParty party) {
      if (party != null) {
         List<MapleCharacter> partymembers = player.getPartyMembersOnline();

         MaplePartyProcessor.getInstance().leaveParty(party, client);
         player.updatePartySearchAvailability(true);
         player.partyOperationUpdate(party, partymembers);
      }
   }

   private void create(MapleCharacter player) {
      MaplePartyProcessor.getInstance().createParty(player, false);
      return;
   }
}
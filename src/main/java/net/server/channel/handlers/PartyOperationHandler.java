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
import net.AbstractMaplePacketHandler;
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
import tools.data.input.SeekableLittleEndianAccessor;

public final class PartyOperationHandler extends AbstractMaplePacketHandler {

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      int operation = slea.readByte();
      MapleCharacter player = c.getPlayer();
      World world = c.getWorldServer();
      MapleParty party = player.getParty();
      switch (operation) {
         case 1: { // create
            MaplePartyProcessor.getInstance().createParty(player, false);
            break;
         }
         case 2: { // leave/disband
            if (party != null) {
               List<MapleCharacter> partymembers = player.getPartyMembersOnline();

               MaplePartyProcessor.getInstance().leaveParty(party, c);
               player.updatePartySearchAvailability(true);
               player.partyOperationUpdate(party, partymembers);
            }
            break;
         }
         case 3: { // join
            int partyid = slea.readInt();

            MapleInviteCoordinator.MapleInviteResult inviteRes = MapleInviteCoordinator.answerInvite(InviteType.PARTY, player.getId(), partyid, true);
            InviteResult res = inviteRes.result;
            if (res == InviteResult.ACCEPTED) {
               MaplePartyProcessor.getInstance().joinParty(player, partyid, false);
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "You couldn't join the party due to an expired invitation request.");
            }
            break;
         }
         case 4: { // invite
            String name = slea.readMapleAsciiString();
            Optional<MapleCharacter> invitedOptional = world.getPlayerStorage().getCharacterByName(name);
            if (invitedOptional.isEmpty()) {
               c.announce(MaplePacketCreator.partyStatusMessage(19));
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
                        c.announce(MaplePacketCreator.partyStatusMessage(22, invited.getName()));
                     }
                  } else {
                     c.announce(MaplePacketCreator.partyStatusMessage(17));
                  }
               } else {
                  c.announce(MaplePacketCreator.partyStatusMessage(16));
               }
            }
            break;
         }
         case 5: { // expel
            int cid = slea.readInt();
            MaplePartyProcessor.getInstance().expelFromParty(party, c, cid);
            break;
         }
         case 6: { // change leader
            int newLeader = slea.readInt();
            MaplePartyCharacter newLeadr = party.getMemberById(newLeader);
            world.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newLeadr);
            break;
         }
      }
   }
}
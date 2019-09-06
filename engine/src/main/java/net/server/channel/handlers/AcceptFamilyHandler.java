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

import client.MapleCharacter;
import client.MapleClient;
import client.MapleFamily;
import client.MapleFamilyEntry;
import client.processor.MapleFamilyProcessor;
import constants.ServerConstants;
import net.AbstractMaplePacketHandler;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.MapleInviteCoordinator.InviteType;
import net.server.coordinator.MapleInviteCoordinator.MapleInviteResult;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author Jay Estrella
 * @author Ubaware
 */
public final class AcceptFamilyHandler extends AbstractMaplePacketHandler {

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      if (!ServerConstants.USE_FAMILY_SYSTEM) {
         return;
      }
      MapleCharacter chr = c.getPlayer();
      int inviterId = slea.readInt();
      slea.readMapleAsciiString();
      boolean accept = slea.readByte() != 0;
      // String inviterName = slea.readMapleAsciiString();
      c.getWorldServer().getPlayerStorage().getCharacterById(inviterId).ifPresent(inviter -> {
         MapleInviteResult inviteResult = MapleInviteCoordinator.answerInvite(InviteType.FAMILY, c.getPlayer().getId(), c.getPlayer(), accept);
         if (inviteResult.result == InviteResult.NOT_FOUND) {
            return; //was never invited. (or expired on server only somehow?)
         }
         if (accept) {
            if (inviter.getFamily() != null) {
               if (chr.getFamily() == null) {
                  MapleFamilyEntry newEntry = new MapleFamilyEntry(inviter.getFamily(), chr.getId(), chr.getName(), chr.getLevel(), chr.getJob());
                  newEntry.setCharacter(chr);
                  if (!newEntry.setSenior(inviter.getFamilyEntry(), true)) {
                     inviter.announce(MaplePacketCreator.sendFamilyMessage(1, 0));
                     return;
                  } else {
                     // save
                     inviter.getFamily().addEntry(newEntry);
                     MapleFamilyProcessor.getInstance().insertNewFamilyRecord(chr.getId(), inviter.getFamily().getID(), inviter.getId(), false);
                  }
               } else { //absorb target family
                  MapleFamilyEntry targetEntry = chr.getFamilyEntry();
                  MapleFamily targetFamily = targetEntry.getFamily();
                  if (targetFamily.getLeader() != targetEntry) {
                     return;
                  }
                  if (inviter.getFamily().getTotalGenerations() + targetFamily.getTotalGenerations() <= ServerConstants.FAMILY_MAX_GENERATIONS) {
                     targetEntry.join(inviter.getFamilyEntry());
                  } else {
                     inviter.announce(MaplePacketCreator.sendFamilyMessage(76, 0));
                     chr.announce(MaplePacketCreator.sendFamilyMessage(76, 0));
                     return;
                  }
               }
            } else { // create new family
               if (chr.getFamily() != null && inviter.getFamily() != null && chr.getFamily().getTotalGenerations() + inviter.getFamily().getTotalGenerations() >= ServerConstants.FAMILY_MAX_GENERATIONS) {
                  inviter.announce(MaplePacketCreator.sendFamilyMessage(76, 0));
                  chr.announce(MaplePacketCreator.sendFamilyMessage(76, 0));
                  return;
               }
               MapleFamily newFamily = new MapleFamily(-1, c.getWorld());
               c.getWorldServer().addFamily(newFamily.getID(), newFamily);
               MapleFamilyEntry inviterEntry = new MapleFamilyEntry(newFamily, inviter.getId(), inviter.getName(), inviter.getLevel(), inviter.getJob());
               inviterEntry.setCharacter(inviter);
               newFamily.setLeader(inviter.getFamilyEntry());
               newFamily.addEntry(inviterEntry);
               if (chr.getFamily() == null) { //completely new family
                  MapleFamilyEntry newEntry = new MapleFamilyEntry(newFamily, chr.getId(), chr.getName(), chr.getLevel(), chr.getJob());
                  newEntry.setCharacter(chr);
                  newEntry.setSenior(inviterEntry, true);
                  // save new family
                  MapleFamilyProcessor.getInstance().insertNewFamilyRecord(inviter.getId(), newFamily.getID(), 0, true);
                  MapleFamilyProcessor.getInstance().insertNewFamilyRecord(chr.getId(), newFamily.getID(), inviter.getId(), false); // char was already saved by setSenior() above
                  MapleFamilyProcessor.getInstance().setMessage(newFamily, "", true);
               } else { //new family for inviter, absorb invitee family
                  MapleFamilyProcessor.getInstance().insertNewFamilyRecord(inviter.getId(), newFamily.getID(), 0, true);
                  MapleFamilyProcessor.getInstance().setMessage(newFamily, "", true);
                  chr.getFamilyEntry().join(inviterEntry);
               }
            }
            c.getPlayer().getFamily().broadcast(MaplePacketCreator.sendFamilyJoinResponse(true, c.getPlayer().getName()), c.getPlayer().getId());
            c.announce(MaplePacketCreator.getSeniorMessage(inviter.getName()));
            c.announce(MaplePacketCreator.getFamilyInfo(chr.getFamilyEntry()));
            chr.getFamilyEntry().updateSeniorFamilyInfo(true);
         } else {
            inviter.announce(MaplePacketCreator.sendFamilyJoinResponse(false, c.getPlayer().getName()));
         }
      });

      c.announce(MaplePacketCreator.sendFamilyMessage(0, 0));
   }
}
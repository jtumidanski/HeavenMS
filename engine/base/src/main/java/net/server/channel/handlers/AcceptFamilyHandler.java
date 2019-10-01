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
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.AcceptFamilyPacket;
import net.server.channel.packet.reader.AcceptFamilyReader;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.MapleInviteCoordinator.InviteType;
import net.server.coordinator.MapleInviteCoordinator.MapleInviteResult;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.family.FamilyJoinResponse;
import tools.packet.family.FamilyMessage;
import tools.packet.family.GetFamilyInfo;
import tools.packet.family.SeniorMessage;

/**
 * @author Jay Estrella
 * @author Ubaware
 */
public final class AcceptFamilyHandler extends AbstractPacketHandler<AcceptFamilyPacket> {
   @Override
   public Class<AcceptFamilyReader> getReaderClass() {
      return AcceptFamilyReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return ServerConstants.USE_FAMILY_SYSTEM;
   }

   @Override
   public void handlePacket(AcceptFamilyPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      // String inviterName = slea.readMapleAsciiString();
      client.getWorldServer().getPlayerStorage().getCharacterById(packet.inviterId()).ifPresent(inviter -> {
         MapleInviteResult inviteResult = MapleInviteCoordinator.answerInvite(InviteType.FAMILY, client.getPlayer().getId(), client.getPlayer(), packet.accept());
         if (inviteResult.result == InviteResult.NOT_FOUND) {
            return; //was never invited. (or expired on server only somehow?)
         }
         if (packet.accept()) {
            if (inviter.getFamily() != null) {
               if (chr.getFamily() == null) {
                  MapleFamilyEntry newEntry = new MapleFamilyEntry(inviter.getFamily(), chr.getId(), chr.getName(), chr.getLevel(), chr.getJob());
                  newEntry.setCharacter(chr);
                  if (!newEntry.setSenior(inviter.getFamilyEntry(), true)) {
                     PacketCreator.announce(inviter, new FamilyMessage(1, 0));
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
                     PacketCreator.announce(inviter, new FamilyMessage(76, 0));
                     PacketCreator.announce(chr, new FamilyMessage(76, 0));
                     return;
                  }
               }
            } else { // create new family
               if (chr.getFamily() != null && inviter.getFamily() != null && chr.getFamily().getTotalGenerations() + inviter.getFamily().getTotalGenerations() >= ServerConstants.FAMILY_MAX_GENERATIONS) {
                  PacketCreator.announce(inviter, new FamilyMessage(76, 0));
                  PacketCreator.announce(chr, new FamilyMessage(76, 0));
                  return;
               }
               MapleFamily newFamily = new MapleFamily(-1, client.getWorld());
               client.getWorldServer().addFamily(newFamily.getID(), newFamily);
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
            MasterBroadcaster.getInstance().sendToFamily(client.getPlayer().getFamily(), character -> PacketCreator.create(new FamilyJoinResponse(true, client.getPlayer().getName())), false, client.getPlayer());
            PacketCreator.announce(client, new SeniorMessage(inviter.getName()));
            PacketCreator.announce(client, new GetFamilyInfo(chr.getFamilyEntry()));
            chr.getFamilyEntry().updateSeniorFamilyInfo(true);
         } else {
            PacketCreator.announce(inviter, new FamilyJoinResponse(false, client.getPlayer().getName()));
         }
      });

      PacketCreator.announce(client, new FamilyMessage(0, 0));
   }
}

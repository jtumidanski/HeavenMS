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

import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.SkillBookPacket;
import net.server.channel.packet.reader.SkillBookReader;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowSkillBookResult;
import tools.packet.stat.EnableActions;

public final class SkillBookHandler extends AbstractPacketHandler<SkillBookPacket> {
   @Override
   public Class<SkillBookReader> getReaderClass() {
      return SkillBookReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!client.getPlayer().isAlive()) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(SkillBookPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (client.tryAcquireClient()) {
         int skillId;
         try {
            MapleInventory inv = player.getInventory(MapleInventoryType.USE);
            Item toUse = inv.getItem(packet.slot());
            if (toUse == null || toUse.id() != packet.itemId()) {
               return;
            }
            Map<String, Integer> skillData = MapleItemInformationProvider.getInstance().getSkillStats(toUse.id(), player.getJob().getId());
            if (skillData == null) {
               return;
            }

            skillId = skillData.get("skillid");
            if (skillId == 0) {
               MasterBroadcaster.getInstance().sendToAllInMap(player.getMap(), new ShowSkillBookResult(player.getId(), skillId, 0, false, false));
            } else {
               SkillFactory.getSkill(skillId).ifPresentOrElse(skill -> {
                  boolean meetsPrerequisiteLevel = (player.getSkillLevel(skill) >= skillData.get("reqSkillLevel") || skillData.get("reqSkillLevel") == 0);
                  boolean notMastered = player.getMasterLevel(skill) < skillData.get("masterLevel");
                  boolean bookCanBeUsed = meetsPrerequisiteLevel && notMastered;
                  boolean success = false;
                  if (bookCanBeUsed) {
                     if (!consumeBook(client, packet.slot(), inv, toUse)) {
                        return;
                     }
                     if (MapleItemInformationProvider.rollSuccessChance(skillData.get("success"))) {
                        success = true;
                        player.changeSkillLevel(skill, player.getSkillLevel(skill), Math.max(skillData.get("masterLevel"), player.getMasterLevel(skill)), -1);
                     } else {
                        success = false;
                     }
                  }
                  boolean finalSuccess = success;
                  MasterBroadcaster.getInstance().sendToAllInMap(player.getMap(), new ShowSkillBookResult(player.getId(), skillId, 0, bookCanBeUsed, finalSuccess));
               }, () -> MasterBroadcaster.getInstance().sendToAllInMap(player.getMap(), new ShowSkillBookResult(player.getId(), skillId, 0, false, false)));
            }
         } finally {
            client.releaseClient();
         }
      }
   }

   /**
    * Consumes the book.
    *
    * @param client the client
    * @param slot   the slot to consume
    * @param inv    the inventory to consume from
    * @param toUse  the item to consume
    * @return true if the book was successfully consumed
    */
   private boolean consumeBook(MapleClient client, short slot, MapleInventory inv, Item toUse) {
      inv.lockInventory();
      try {
         Item used = inv.getItem(slot);
         if (used != toUse || toUse.quantity() < 1) {
            return false;
         }

         MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, slot, (short) 1, false);
      } finally {
         inv.unlockInventory();
      }
      return true;
   }
}

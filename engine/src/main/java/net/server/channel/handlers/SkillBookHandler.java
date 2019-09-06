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
import net.AbstractMaplePacketHandler;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public final class SkillBookHandler extends AbstractMaplePacketHandler {
   @Override
   public final void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient c) {
      if (!c.getPlayer().isAlive()) {
         c.announce(MaplePacketCreator.enableActions());
         return;
      }

      accessor.readInt();
      short slot = accessor.readShort();
      int itemId = accessor.readInt();

      MapleCharacter player = c.getPlayer();
      if (c.tryAcquireClient()) {
         int skillId;
         try {
            MapleInventory inv = player.getInventory(MapleInventoryType.USE);
            Item toUse = inv.getItem(slot);
            if (toUse == null || toUse.getItemId() != itemId) {
               return;
            }
            Map<String, Integer> skillData = MapleItemInformationProvider.getInstance().getSkillStats(toUse.getItemId(), player.getJob().getId());
            if (skillData == null) {
               return;
            }

            skillId = skillData.get("skillid");
            if (skillId == 0) {
               player.getMap().broadcastMessage(MaplePacketCreator.skillBookResult(player, skillId, 0, false, false));
            } else {
               SkillFactory.getSkill(skillId).ifPresentOrElse(skill -> {
                  boolean meetsPrerequisiteLevel = (player.getSkillLevel(skill) >= skillData.get("reqSkillLevel") || skillData.get("reqSkillLevel") == 0);
                  boolean notMastered = player.getMasterLevel(skill) < skillData.get("masterLevel");
                  boolean bookCanBeUsed = meetsPrerequisiteLevel && notMastered;
                  boolean success = false;
                  if (bookCanBeUsed) {
                     if (!consumeBook(c, slot, inv, toUse)) {
                        return;
                     }
                     if (MapleItemInformationProvider.rollSuccessChance(skillData.get("success"))) {
                        success = true;
                        player.changeSkillLevel(skill, player.getSkillLevel(skill), Math.max(skillData.get("masterLevel"), player.getMasterLevel(skill)), -1);
                     } else {
                        success = false;
                     }
                  }
                  player.getMap().broadcastMessage(MaplePacketCreator.skillBookResult(player, skillId, 0, bookCanBeUsed, success));
               }, () -> player.getMap().broadcastMessage(MaplePacketCreator.skillBookResult(player, skillId, 0, false, false)));
            }
         } finally {
            c.releaseClient();
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
         if (used != toUse || toUse.getQuantity() < 1) {
            return false;
         }

         MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, slot, (short) 1, false);
      } finally {
         inv.unlockInventory();
      }
      return true;
   }
}
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

import java.util.Optional;
import java.util.stream.IntStream;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleKeyBinding;
import client.Skill;
import client.SkillFactory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.AbstractMaplePacketHandler;
import tools.data.input.SeekableLittleEndianAccessor;

public final class KeymapChangeHandler extends AbstractMaplePacketHandler {
   @Override
   public final void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient c) {
      if (accessor.available() >= 8) {
         int mode = accessor.readInt();
         if (mode == 0) {
            int numChanges = accessor.readInt();
            IntStream.generate(() -> 1).limit(numChanges).forEach(id -> changeKeyBinding(new KeyTypeAction(accessor), c.getPlayer()));
         } else if (mode == 1) { // Auto HP Potion
            int itemID = accessor.readInt();
            if (itemID != 0 && c.getPlayer().getInventory(MapleInventoryType.USE).findById(itemID) == null) {
               c.disconnect(false, false); // Don't let them send a packet with a use item they dont have.
               return;
            }
            c.getPlayer().changeKeybinding(91, new MapleKeyBinding(7, itemID));
         } else if (mode == 2) { // Auto MP Potion
            int itemID = accessor.readInt();
            if (itemID != 0 && c.getPlayer().getInventory(MapleInventoryType.USE).findById(itemID) == null) {
               c.disconnect(false, false); // Don't let them send a packet with a use item they dont have.
               return;
            }
            c.getPlayer().changeKeybinding(92, new MapleKeyBinding(7, itemID));
         }
      }
   }

   /**
    * Reads a key binding change and makes it happen (if it is a valid change).
    * @param keyTypeAction information about the new key binding
    * @param character the character being changed
    */
   private void changeKeyBinding(KeyTypeAction keyTypeAction, MapleCharacter character) {
      if (keyTypeAction.getType() == 1) {
         Optional<Skill> skill = SkillFactory.getSkill(keyTypeAction.getAction());
         if (skill.isPresent()) {
            int skillId = skill.get().getId();
            boolean isBannedSkill = GameConstants.bannedBindSkills(skillId);
            boolean notAGMWithGMSkills = !character.isGM() && GameConstants.isGMSkills(skillId);
            boolean hasSkillsNotInTree = (!GameConstants.isInJobTree(skillId, character.getJob().getId()) && !character.isGM());
            if (isBannedSkill || notAGMWithGMSkills || hasSkillsNotInTree) { //for those skills are are "technically" in the beginner tab, like bamboo rain in Dojo or skills you find in PYPQ
               return;
            }
         }
      }
      character.changeKeybinding(keyTypeAction.getKey(), new MapleKeyBinding(keyTypeAction.getType(), keyTypeAction.getAction()));
   }

   private class KeyTypeAction {
      private int key;

      private int type;

      private int action;

      public KeyTypeAction(int key, int type, int action) {
         this.key = key;
         this.type = type;
         this.action = action;
      }

      public KeyTypeAction(SeekableLittleEndianAccessor accessor) {
         this.key = accessor.readInt();
         this.type = accessor.readByte();
         this.action = accessor.readInt();
      }

      public int getKey() {
         return key;
      }

      public int getType() {
         return type;
      }

      public int getAction() {
         return action;
      }
   }
}

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

import java.util.Arrays;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleKeyBinding;
import client.Skill;
import client.SkillFactory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.keymap.AutoHPKeymapChangePacket;
import net.server.channel.packet.keymap.AutoMPKeymapChangePacket;
import net.server.channel.packet.keymap.BaseKeymapChangePacket;
import net.server.channel.packet.keymap.KeyTypeAction;
import net.server.channel.packet.keymap.RegularKeymapChangePacket;
import net.server.channel.packet.reader.KeymapChangeReader;

public final class KeymapChangeHandler extends AbstractPacketHandler<BaseKeymapChangePacket, KeymapChangeReader> {
   @Override
   public Class<KeymapChangeReader> getReaderClass() {
      return KeymapChangeReader.class;
   }

   @Override
   public void handlePacket(BaseKeymapChangePacket packet, MapleClient client) {
      if (packet.available()) {
         if (packet instanceof RegularKeymapChangePacket) {
            Arrays.stream(((RegularKeymapChangePacket) packet).changes()).forEach(change -> changeKeyBinding(change, client.getPlayer()));
         } else if (packet instanceof AutoHPKeymapChangePacket) {
            if (((AutoHPKeymapChangePacket) packet).itemId() != 0 && client.getPlayer().getInventory(MapleInventoryType.USE).findById(((AutoHPKeymapChangePacket) packet).itemId()) == null) {
               client.disconnect(false, false); // Don't let them send a packet with a use item they dont have.
               return;
            }
            client.getPlayer().changeKeybinding(91, new MapleKeyBinding(7, ((AutoHPKeymapChangePacket) packet).itemId()));
         } else if (packet instanceof AutoMPKeymapChangePacket) {
            if (((AutoMPKeymapChangePacket) packet).itemId() != 0 && client.getPlayer().getInventory(MapleInventoryType.USE).findById(((AutoMPKeymapChangePacket) packet).itemId()) == null) {
               client.disconnect(false, false); // Don't let them send a packet with a use item they dont have.
               return;
            }
            client.getPlayer().changeKeybinding(92, new MapleKeyBinding(7, ((AutoMPKeymapChangePacket) packet).itemId()));
         }
      }
   }

   /**
    * Reads a key binding change and makes it happen (if it is a valid change).
    *
    * @param keyTypeAction information about the new key binding
    * @param character     the character being changed
    */
   private void changeKeyBinding(KeyTypeAction keyTypeAction, MapleCharacter character) {
      if (keyTypeAction.theType() == 1) {
         Optional<Skill> skill = SkillFactory.getSkill(keyTypeAction.action());
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
      character.changeKeybinding(keyTypeAction.key(), new MapleKeyBinding(keyTypeAction.theType(), keyTypeAction.action()));
   }
}

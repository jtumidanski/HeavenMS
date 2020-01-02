package net.server.channel.handlers;

import java.util.Arrays;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.KeyBinding;
import client.Skill;
import client.SkillFactory;
import client.inventory.MapleInventoryType;
import constants.game.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.keymap.AutoHPKeymapChangePacket;
import net.server.channel.packet.keymap.AutoMPKeymapChangePacket;
import net.server.channel.packet.keymap.BaseKeymapChangePacket;
import net.server.channel.packet.keymap.KeyTypeAction;
import net.server.channel.packet.keymap.RegularKeymapChangePacket;
import net.server.channel.packet.reader.KeymapChangeReader;

public final class KeymapChangeHandler extends AbstractPacketHandler<BaseKeymapChangePacket> {
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
            client.getPlayer().changeKeyBinding(91, new KeyBinding(7, ((AutoHPKeymapChangePacket) packet).itemId()));
         } else if (packet instanceof AutoMPKeymapChangePacket) {
            if (((AutoMPKeymapChangePacket) packet).itemId() != 0 && client.getPlayer().getInventory(MapleInventoryType.USE).findById(((AutoMPKeymapChangePacket) packet).itemId()) == null) {
               client.disconnect(false, false); // Don't let them send a packet with a use item they dont have.
               return;
            }
            client.getPlayer().changeKeyBinding(92, new KeyBinding(7, ((AutoMPKeymapChangePacket) packet).itemId()));
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
            if (isBannedSkill || notAGMWithGMSkills || hasSkillsNotInTree) {
               return;
            }
         }
      }
      character.changeKeyBinding(keyTypeAction.key(), new KeyBinding(keyTypeAction.theType(), keyTypeAction.action()));
   }
}

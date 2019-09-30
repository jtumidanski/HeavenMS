/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    Copyleft (L) 2016 - 2018 RonanLana (HeavenMS)

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
package client.processor;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.autoban.AutobanFactory;
import constants.GameConstants;
import constants.skills.Aran;
import server.ThreadManager;
import tools.FilePrinter;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

/**
 * @author RonanLana - synchronization of SP transaction modules
 */
public class AssignSPProcessor {

   public static boolean canSPAssign(MapleClient c, int skillid) {
      if (skillid == Aran.HIDDEN_FULL_DOUBLE || skillid == Aran.HIDDEN_FULL_TRIPLE || skillid == Aran.HIDDEN_OVER_DOUBLE || skillid == Aran.HIDDEN_OVER_TRIPLE) {
         PacketCreator.announce(c, new EnableActions());
         return false;
      }

      MapleCharacter player = c.getPlayer();
      int remainingSp = player.getRemainingSps()[GameConstants.getSkillBook(skillid / 10000)];
      boolean isBeginnerSkill = false;
      if ((!GameConstants.isPqSkillMap(player.getMapId()) && GameConstants.isPqSkill(skillid)) || (!player.isGM() && GameConstants.isGMSkills(skillid)) || (!GameConstants.isInJobTree(skillid, player.getJob().getId()) && !player.isGM())) {
         AutobanFactory.PACKET_EDIT.alert(player, "tried to packet edit in distributing sp.");
         FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to use skill " + skillid + " without it being in their job.");

         final MapleClient client = c;
         ThreadManager.getInstance().newTask(new Runnable() {
            @Override
            public void run() {
               client.disconnect(true, false);
            }
         });

         return false;
      }

      return true;
   }

   public static void SPAssignAction(MapleClient c, int skillid) {
      c.lockClient();
      try {
         if (!canSPAssign(c, skillid)) {
            return;
         }

         MapleCharacter player = c.getPlayer();
         int remainingSp = player.getRemainingSps()[GameConstants.getSkillBook(skillid / 10000)];
         boolean isBeginnerSkill = false;

         if (skillid % 10000000 > 999 && skillid % 10000000 < 1003) {
            int total = 0;
            for (int i = 0; i < 3; i++) {
               total += SkillFactory.applyForSkill(player, player.getJobType() * 10000000 + 1000 + i, (skill, skillLevel) -> skillLevel, 0);
            }
            remainingSp = Math.min((player.getLevel() - 1), 6) - total;
            isBeginnerSkill = true;
         }

         Optional<Skill> skillOptional = SkillFactory.getSkill(skillid);
         if (skillOptional.isPresent()) {
            Skill skill = skillOptional.get();
            int curLevel = player.getSkillLevel(skill);

            int masterLevel = player.getMasterLevel(skill);
            if ((remainingSp > 0 && curLevel + 1 <= (skill.isFourthJob() ? masterLevel : skill.getMaxLevel()))) {
               if (!isBeginnerSkill) {
                  player.gainSp(-1, GameConstants.getSkillBook(skillid / 10000), false);
               } else {
                  PacketCreator.announce(c, new EnableActions());
               }

               long skillExpiration = player.getSkillExpiration(skill);
               if (skill.getId() == Aran.FULL_SWING) {
                  player.changeSkillLevel(skill, (byte) (curLevel + 1), masterLevel, skillExpiration);
                  SkillFactory.getSkill(Aran.HIDDEN_FULL_DOUBLE).ifPresent(hfd -> player.changeSkillLevel(hfd, player.getSkillLevel(skill), masterLevel, skillExpiration));
                  SkillFactory.getSkill(Aran.HIDDEN_FULL_TRIPLE).ifPresent(hfd -> player.changeSkillLevel(hfd, player.getSkillLevel(skill), masterLevel, skillExpiration));
               } else if (skill.getId() == Aran.OVER_SWING) {
                  player.changeSkillLevel(skill, (byte) (curLevel + 1), masterLevel, player.getSkillExpiration(skill));
                  SkillFactory.getSkill(Aran.HIDDEN_FULL_DOUBLE).ifPresent(hfd -> player.changeSkillLevel(hfd, player.getSkillLevel(skill), masterLevel, skillExpiration));
                  SkillFactory.getSkill(Aran.HIDDEN_FULL_TRIPLE).ifPresent(hfd -> player.changeSkillLevel(hfd, player.getSkillLevel(skill), masterLevel, skillExpiration));
               } else {
                  player.changeSkillLevel(skill, (byte) (curLevel + 1), masterLevel, skillExpiration);
               }
            }
         }
      } finally {
         c.unlockClient();
      }
   }
}

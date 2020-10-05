package client.processor.stat;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.autoban.AutoBanFactory;
import constants.game.GameConstants;
import constants.skills.Aran;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public class AssignSPProcessor {

   public static boolean canSPAssign(MapleClient c, int skillId) {
      if (skillId == Aran.HIDDEN_FULL_DOUBLE || skillId == Aran.HIDDEN_FULL_TRIPLE || skillId == Aran.HIDDEN_OVER_DOUBLE || skillId == Aran.HIDDEN_OVER_TRIPLE) {
         PacketCreator.announce(c, new EnableActions());
         return false;
      }

      MapleCharacter player = c.getPlayer();
      if ((!GameConstants.isPqSkillMap(player.getMapId()) && GameConstants.isPqSkill(skillId)) || (!player.isGM() && GameConstants.isGMSkills(skillId)) || (!GameConstants.isInJobTree(skillId, player.getJob().getId()) && !player.isGM())) {
         AutoBanFactory.PACKET_EDIT.alert(player, "tried to packet edit in distributing sp.");
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, c.getPlayer().getName() + " tried to use skill " + skillId + " without it being in their job.");

         c.disconnect(true, false);
      }

      return true;
   }

   public static void SPAssignAction(MapleClient c, int skillId) {
      c.lockClient();
      try {
         if (!canSPAssign(c, skillId)) {
            return;
         }

         MapleCharacter player = c.getPlayer();
         int remainingSp = player.getRemainingSps()[GameConstants.getSkillBook(skillId / 10000)];
         boolean isBeginnerSkill = false;

         if (skillId % 10000000 > 999 && skillId % 10000000 < 1003) {
            int total = 0;
            for (int i = 0; i < 3; i++) {
               total += SkillFactory.applyForSkill(player, player.getJobType() * 10000000 + 1000 + i, (skill, skillLevel) -> skillLevel, 0);
            }
            remainingSp = Math.min((player.getLevel() - 1), 6) - total;
            isBeginnerSkill = true;
         }

         Optional<Skill> skillOptional = SkillFactory.getSkill(skillId);
         if (skillOptional.isPresent()) {
            Skill skill = skillOptional.get();
            int curLevel = player.getSkillLevel(skill);

            int masterLevel = player.getMasterLevel(skill);
            if ((remainingSp > 0 && curLevel + 1 <= (skill.isFourthJob() ? masterLevel : skill.getMaxLevel()))) {
               if (!isBeginnerSkill) {
                  player.gainSp(-1, GameConstants.getSkillBook(skillId / 10000), false);
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

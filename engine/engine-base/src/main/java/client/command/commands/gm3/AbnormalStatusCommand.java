package client.command.commands.gm3;

import java.util.Collections;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleAbnormalStatus;
import client.command.Command;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class AbnormalStatusCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ABNORMAL_STATUS_COMMAND_SYNTAX"));
         return;
      }

      MapleAbnormalStatus disease = null;
      MobSkill skill = null;

      switch (params[0].toUpperCase()) {
         case "SLOW" -> {
            disease = MapleAbnormalStatus.SLOW;
            skill = MobSkillFactory.getMobSkill(126, 7);
         }
         case "SEDUCE" -> {
            disease = MapleAbnormalStatus.SEDUCE;
            skill = MobSkillFactory.getMobSkill(128, 7);
         }
         case "ZOMBIFY" -> {
            disease = MapleAbnormalStatus.ZOMBIFY;
            skill = MobSkillFactory.getMobSkill(133, 1);
         }
         case "CONFUSE" -> {
            disease = MapleAbnormalStatus.CONFUSE;
            skill = MobSkillFactory.getMobSkill(132, 2);
         }
         case "STUN" -> {
            disease = MapleAbnormalStatus.STUN;
            skill = MobSkillFactory.getMobSkill(123, 7);
         }
         case "POISON" -> {
            disease = MapleAbnormalStatus.POISON;
            skill = MobSkillFactory.getMobSkill(125, 5);
         }
         case "SEAL" -> {
            disease = MapleAbnormalStatus.SEAL;
            skill = MobSkillFactory.getMobSkill(120, 1);
         }
         case "DARKNESS" -> {
            disease = MapleAbnormalStatus.DARKNESS;
            skill = MobSkillFactory.getMobSkill(121, 1);
         }
         case "WEAKEN" -> {
            disease = MapleAbnormalStatus.WEAKEN;
            skill = MobSkillFactory.getMobSkill(122, 1);
         }
         case "CURSE" -> {
            disease = MapleAbnormalStatus.CURSE;
            skill = MobSkillFactory.getMobSkill(124, 1);
         }
      }

      if (disease == null) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ABNORMAL_STATUS_COMMAND_SYNTAX"));
         return;
      }

      for (MapleMapObject mmo : player.getMap().getMapObjectsInRange(player.position(), 777777.7, Collections.singletonList(MapleMapObjectType.PLAYER))) {
         MapleCharacter chr = (MapleCharacter) mmo;

         if (chr.getId() != player.getId()) {
            chr.giveAbnormalStatus(disease, skill);
         }
      }
   }
}

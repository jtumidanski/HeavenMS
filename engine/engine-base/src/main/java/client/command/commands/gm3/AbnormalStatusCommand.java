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

public class AbnormalStatusCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !debuff SLOW|SEDUCE|ZOMBIFY|CONFUSE|STUN|POISON|SEAL|DARKNESS|WEAKEN|CURSE");
         return;
      }

      MapleAbnormalStatus disease = null;
      MobSkill skill = null;

      switch (params[0].toUpperCase()) {
         case "SLOW":
            disease = MapleAbnormalStatus.SLOW;
            skill = MobSkillFactory.getMobSkill(126, 7);
            break;

         case "SEDUCE":
            disease = MapleAbnormalStatus.SEDUCE;
            skill = MobSkillFactory.getMobSkill(128, 7);
            break;

         case "ZOMBIFY":
            disease = MapleAbnormalStatus.ZOMBIFY;
            skill = MobSkillFactory.getMobSkill(133, 1);
            break;

         case "CONFUSE":
            disease = MapleAbnormalStatus.CONFUSE;
            skill = MobSkillFactory.getMobSkill(132, 2);
            break;

         case "STUN":
            disease = MapleAbnormalStatus.STUN;
            skill = MobSkillFactory.getMobSkill(123, 7);
            break;

         case "POISON":
            disease = MapleAbnormalStatus.POISON;
            skill = MobSkillFactory.getMobSkill(125, 5);
            break;

         case "SEAL":
            disease = MapleAbnormalStatus.SEAL;
            skill = MobSkillFactory.getMobSkill(120, 1);
            break;

         case "DARKNESS":
            disease = MapleAbnormalStatus.DARKNESS;
            skill = MobSkillFactory.getMobSkill(121, 1);
            break;

         case "WEAKEN":
            disease = MapleAbnormalStatus.WEAKEN;
            skill = MobSkillFactory.getMobSkill(122, 1);
            break;

         case "CURSE":
            disease = MapleAbnormalStatus.CURSE;
            skill = MobSkillFactory.getMobSkill(124, 1);
            break;
      }

      if (disease == null) {
         player.yellowMessage("Syntax: !debuff SLOW|SEDUCE|ZOMBIFY|CONFUSE|STUN|POISON|SEAL|DARKNESS|WEAKEN|CURSE");
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

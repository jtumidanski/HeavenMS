package client.command.commands.gm2;

import java.io.File;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.command.Command;
import constants.MapleJob;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import tools.I18nMessage;
import tools.MessageBroadcaster;

public class MaxSkillCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (MapleData skill_ : MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "String.wz"))
            .getData("Skill.img").getChildren()) {
         try {
            int skillId = Integer.parseInt(skill_.getName());
            SkillFactory.getSkill(skillId)
                  .ifPresent(skill -> player.changeSkillLevel(skill, (byte) skill.getMaxLevel(), skill.getMaxLevel(), -1));
         } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            break;
         } catch (NullPointerException ignored) {
         }
      }

      if (player.getJob().isA(MapleJob.ARAN1) || player.getJob().isA(MapleJob.LEGEND)) {
         SkillFactory.getSkill(5001005).ifPresent(skill -> player.changeSkillLevel(skill, (byte) -1, -1, -1));
      } else {
         SkillFactory.getSkill(21001001).ifPresent(skill -> player.changeSkillLevel(skill, (byte) -1, -1, -1));
      }

      MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("MAX_SKILL_COMMAND_SUCCESS"));
   }
}

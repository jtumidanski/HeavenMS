package client.command.commands.gm2;

import java.io.File;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.SkillFactory;
import client.command.Command;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class ResetSkillCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (MapleData skill_ : MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "String.wz")).getData("Skill.img").getChildren()) {
         try {
            SkillFactory.getSkill(Integer.parseInt(skill_.getName())).ifPresent(skill -> player.changeSkillLevel(skill, (byte) 0, skill.getMaxLevel(), -1));
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

      MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("RESET_SKILL_COMMAND_SUCCESS"));
   }
}

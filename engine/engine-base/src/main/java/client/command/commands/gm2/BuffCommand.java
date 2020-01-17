package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class BuffCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("BUFF_COMMAND_SYNTAX"));
         return;
      }
      int skillId = Integer.parseInt(params[0]);
      SkillFactory.getSkill(skillId).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
   }
}

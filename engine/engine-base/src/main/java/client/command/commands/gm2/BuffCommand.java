package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.command.Command;

public class BuffCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !buff <buff id>");
         return;
      }
      int skillId = Integer.parseInt(params[0]);
      SkillFactory.getSkill(skillId).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
   }
}

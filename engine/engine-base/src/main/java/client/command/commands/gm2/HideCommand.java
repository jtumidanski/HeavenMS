package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.command.Command;

public class HideCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      SkillFactory.getSkill(9101004).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
   }
}

package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.command.Command;

public class BuffMapCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      SkillFactory.getSkill(9101001).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
      SkillFactory.getSkill(9101002).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
      SkillFactory.getSkill(9101003).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
      SkillFactory.getSkill(9101008).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
      SkillFactory.getSkill(1005).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
   }
}

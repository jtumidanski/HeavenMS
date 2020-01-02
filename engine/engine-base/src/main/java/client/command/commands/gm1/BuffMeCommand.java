package client.command.commands.gm1;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.command.Command;

public class BuffMeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();


      SkillFactory.getSkill(4101004).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
      SkillFactory.getSkill(2311003).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
      SkillFactory.getSkill(1301007).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
      SkillFactory.getSkill(2301004).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
      SkillFactory.getSkill(1005).ifPresent(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
      player.healHpMp();
   }
}

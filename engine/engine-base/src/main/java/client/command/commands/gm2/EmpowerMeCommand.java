package client.command.commands.gm2;

import java.util.Arrays;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.command.Command;

public class EmpowerMeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      final int[] array = {2311003, 2301004, 1301007, 4101004, 2001002, 1101007, 1005, 2301003, 5121009, 1111002, 4111001, 4111002, 4211003, 4211005, 1321000, 2321004, 3121002};
      Arrays.stream(array)
            .mapToObj(SkillFactory::getSkill)
            .flatMap(Optional::stream)
            .forEach(skill -> skill.getEffect(skill.getMaxLevel()).applyTo(player));
   }
}

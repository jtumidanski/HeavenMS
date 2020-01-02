package client.command.commands.gm1;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleMonster;

public class MobHpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (MapleMonster monster : player.getMap().getAllMonsters()) {
         if (monster != null && monster.getHp() > 0) {
            player.yellowMessage(monster.getName() + " (" + monster.id() + ") has " + monster.getHp() + " / " + monster.getMaxHp() + " HP.");

         }
      }
   }
}

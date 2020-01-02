package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleLifeFactory;

public class CakeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleLifeFactory.getMonster(9400606).ifPresent(monster -> {
         MapleCharacter player = client.getPlayer();
         if (params.length == 1) {
            double mobHp = Double.parseDouble(params[0]);
            int newHp = (mobHp <= 0) ? Integer.MAX_VALUE : ((mobHp > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) mobHp);

            monster.setStartingHp(newHp);
         }
         player.getMap().spawnMonsterOnGroundBelow(monster, player.position());
      });
   }
}

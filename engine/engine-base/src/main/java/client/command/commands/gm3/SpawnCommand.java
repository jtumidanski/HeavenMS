package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleLifeFactory;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class SpawnCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter player = client.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SPAWN_COMMAND_SYNTAX"));
         return;
      }

      int monsterId = Integer.parseInt(params[0]);
      MapleLifeFactory.getMonster(monsterId).ifPresent(monster -> {
         if (params.length == 2) {
            for (int i = 0; i < Integer.parseInt(params[1]); i++) {
               player.getMap().spawnMonsterOnGroundBelow(monster, player.position());
            }
         } else {
            player.getMap().spawnMonsterOnGroundBelow(monster, player.position());
         }
      });
   }
}

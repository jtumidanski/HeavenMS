package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MaplePlayerNPC;

public class SpawnAllPlayerNpcCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      MaplePlayerNPC.multicastSpawnPlayerNPC(player.getMapId(), player.getWorld());
   }
}

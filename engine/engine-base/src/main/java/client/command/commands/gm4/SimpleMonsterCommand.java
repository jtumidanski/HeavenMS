package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleLifeFactory;

public class SimpleMonsterCommand extends Command {
   private int monsterId;

   public SimpleMonsterCommand(int monsterId) {
      this.monsterId = monsterId;
      this.description = "";
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter character = client.getPlayer();
      MapleLifeFactory.getMonster(monsterId).ifPresent(monster -> character.getMap().spawnMonsterOnGroundBelow(monster, character.position()));
   }
}

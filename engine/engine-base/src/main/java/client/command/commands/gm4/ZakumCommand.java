package client.command.commands.gm4;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleLifeFactory;

public class ZakumCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter player = client.getPlayer();
      MapleLifeFactory.getMonster(8800000).ifPresent(monster -> player.getMap().spawnFakeMonsterOnGroundBelow(monster, player.position()));
      IntStream.range(8800003, 8800011)
            .mapToObj(MapleLifeFactory::getMonster)
            .flatMap(Optional::stream)
            .filter(Objects::nonNull)
            .forEach(monster -> player.getMap().spawnMonsterOnGroundBelow(monster, player.position()));
   }
}

package client.command.commands.gm2;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleLifeFactory;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class BombCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter player = client.getPlayer();
      if (params.length > 0) {
         Optional<MapleCharacter> victim = client.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
         if (victim.isPresent()) {
            MapleLifeFactory.getMonster(9300166).ifPresent(monster -> victim.get().getMap().spawnMonsterOnGroundBelow(monster, victim.get().position()));
            MessageBroadcaster.getInstance().sendWorldServerNotice(client.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, I18nMessage.from("USE_BOMB").with(player.getName(), victim.get().getName()));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0]));
         }
      } else {
         MapleLifeFactory.getMonster(9300166).ifPresent(monster -> player.getMap().spawnMonsterOnGroundBelow(monster, player.position()));
      }
   }
}

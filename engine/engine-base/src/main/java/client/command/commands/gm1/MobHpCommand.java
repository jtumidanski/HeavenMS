package client.command.commands.gm1;

import java.util.Objects;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleMonster;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class MobHpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      player.getMap().getAllMonsters().stream()
            .filter(Objects::nonNull)
            .filter(monster -> monster.getHp() > 0)
            .forEach(monster -> sendMonsterHp(player, monster));
   }

   protected void sendMonsterHp(MapleCharacter player, MapleMonster monster) {
      MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("MOB_HP_COMMAND_PART").with(monster.getName(), monster.id(), monster.getHp(), monster.getMaxHp()));
   }
}

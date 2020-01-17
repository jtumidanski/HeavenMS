package client.command.commands.gm1;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleMonster;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class BossHpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (MapleMonster monster : player.getMap().getAllMonsters()) {
         if (monster != null && monster.isBoss() && monster.getHp() > 0) {
            long percent = monster.getHp() * 100L / monster.getMaxHp();
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < 100; i++) {
               bar.append(i < percent ? "|" : ".");
            }
            bar.append("]");
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("BOSS_HP_COMMAND_PART").with(monster.getName(), monster.id(), percent));
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("BOSS_HP_COMMAND_TOTAL_HP").with(bar));
         }
      }
   }
}

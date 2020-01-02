package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class MaxHpMpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      Optional<MapleCharacter> victim = Optional.of(player);

      int statUpdate = 1;
      if (params.length >= 2) {
         victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
         statUpdate = Integer.parseInt(params[1]);
      } else if (params.length == 1) {
         statUpdate = Integer.parseInt(params[0]);
      } else {
         player.yellowMessage("Syntax: !maxhpmp [<player name>] <value>");
      }

      if (victim.isPresent()) {
         int extraHp = victim.get().getCurrentMaxHp() - victim.get().getClientMaxHp();
         int extraMp = victim.get().getCurrentMaxMp() - victim.get().getClientMaxMp();
         statUpdate = Math.max(1 + Math.max(extraHp, extraMp), statUpdate);

         int maxHpUpdate = statUpdate - extraHp;
         int maxMpUpdate = statUpdate - extraMp;
         victim.get().updateMaxHpMaxMp(maxHpUpdate, maxMpUpdate);
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found on this world.");
      }
   }
}

package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MapleLogger;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class IgnoreCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !ignore <ign>");
         return;
      }
      Optional<MapleCharacter> victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
      if (victim.isEmpty()) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found on this world.");
         return;
      }
      boolean monitored_ = MapleLogger.ignored.contains(victim.get().getId());
      if (monitored_) {
         MapleLogger.ignored.remove(victim.get().getId());
      } else {
         MapleLogger.ignored.add(victim.get().getId());
      }
      player.yellowMessage(victim.get().getName() + " is " + (!monitored_ ? "now being ignored." : "no longer being ignored."));
      String message_ = player.getName() + (!monitored_ ? " has started ignoring " : " has stopped ignoring ") + victim.get().getName() + ".";
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, message_);
   }
}

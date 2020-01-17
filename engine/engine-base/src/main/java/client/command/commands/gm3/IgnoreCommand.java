package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MapleLogger;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class IgnoreCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("IGNORE_TOGGLE_COMMAND_SYNTAX"));
         return;
      }
      Optional<MapleCharacter> victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
      if (victim.isEmpty()) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0]));
         return;
      }
      boolean monitored_ = MapleLogger.ignored.contains(victim.get().getId());
      if (monitored_) {
         MapleLogger.ignored.remove(victim.get().getId());
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("IGNORE_TOGGLE_COMMAND_ON").with(victim.get().getId()));
      } else {
         MapleLogger.ignored.add(victim.get().getId());
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("IGNORE_TOGGLE_COMMAND_OFF").with(victim.get().getId()));
      }
      String message_ = player.getName() + (!monitored_ ? " has started ignoring " : " has stopped ignoring ") + victim.get().getName() + ".";
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, message_);
   }
}

package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.I18nMessage;
import tools.MapleLogger;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class MonitorCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("MONITOR_TOGGLE_COMMAND_SYNTAX"));
         return;
      }
      Optional<MapleCharacter> victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
      if (victim.isEmpty()) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0]));
         return;
      }
      boolean monitored = MapleLogger.monitored.contains(victim.get().getId());
      if (monitored) {
         MapleLogger.monitored.remove(victim.get().getId());
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("MONITOR_TOGGLE_COMMAND_ON").with(victim.get().getId()));
      } else {
         MapleLogger.monitored.add(victim.get().getId());
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("MONITOR_TOGGLE_COMMAND_OFF").with(victim.get().getId()));
      }

      if (!monitored) {
         MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, I18nMessage.from("MONITOR_COMMAND_START").with(player.getName(), victim.get().getName()));
      } else {
         MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, I18nMessage.from("MONITOR_COMMAND_END").with(player.getName(), victim.get().getName()));
      }
   }
}

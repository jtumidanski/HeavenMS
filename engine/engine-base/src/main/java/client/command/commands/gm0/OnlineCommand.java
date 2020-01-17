package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.channel.Channel;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class OnlineCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (Channel ch : Server.getInstance().getChannelsFromWorld(player.getWorld())) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ONLINE_COMMAND_TITLE").with(ch.getId()));
         for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
            if (!chr.isGM()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ONLINE_COMMAND_BODY").with(chr.getName(), chr.getMap().getMapName()));
            }
         }
      }
   }
}

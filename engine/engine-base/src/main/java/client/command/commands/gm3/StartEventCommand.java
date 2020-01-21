package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.events.gm.MapleEvent;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.foreigneffect.ShowTitleEarned;

public class StartEventCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      int players = 50;
      if (params.length > 1) {
         players = Integer.parseInt(params[0]);
      }
      c.getChannelServer().setEvent(new MapleEvent(player.getMapId(), players));
      Server.getInstance().broadcastMessage(c.getWorld(), PacketCreator.create(new ShowTitleEarned(
            "[Event] An event has started on "
                  + player.getMap().getMapName()
                  + " and will allow "
                  + players
                  + " players to join. Type @joinevent to participate.")));
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EVENT_START").with(player.getMap().getMapName(), players));
   }
}

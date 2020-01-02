package client.command.commands.gm2;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class SummonCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !warphere <player name>");
         return;
      }

      MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0])
            .or(() -> getCharacterAcrossWorld(c, params[0]))
            .orElseThrow();

      if (victim != null) {
         if (!victim.isLoggedInWorld()) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Player currently not logged in or unreachable.");
            return;
         }

         if (player.getClient().getChannel() != victim.getClient().getChannel()) {//And then change channel if needed.
            MessageBroadcaster.getInstance().sendServerNotice(victim, ServerNoticeType.NOTICE, "Changing channel, please wait a moment.");
            victim.getClient().changeChannel(player.getClient().getChannel());
         }

         try {
            for (int i = 0; i < 7; i++) {   // poll for a while until the player reconnects
               if (victim.isLoggedInWorld()) break;
               Thread.sleep(1777);
            }
         } catch (InterruptedException ignored) {
         }

         MapleMap map = player.getMap();
         victim.saveLocationOnWarp();
         victim.forceChangeMap(map, map.findClosestPortal(player.position()));
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Unknown player.");
      }
   }

   private Optional<MapleCharacter> getCharacterAcrossWorld(MapleClient c, String characterName) {
      return Server.getInstance().getChannelsFromWorld(c.getWorld()).stream()
            .map(channel -> channel.getPlayerStorage().getCharacterByName(characterName))
            .flatMap(Optional::stream)
            .findFirst();
   }
}

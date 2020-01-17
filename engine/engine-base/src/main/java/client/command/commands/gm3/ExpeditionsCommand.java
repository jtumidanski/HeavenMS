package client.command.commands.gm3;

import java.util.List;
import java.util.Map.Entry;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.channel.Channel;
import server.expeditions.MapleExpedition;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class ExpeditionsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (Channel ch : Server.getInstance().getChannelsFromWorld(c.getWorld())) {
         List<MapleExpedition> expeditions = ch.getExpeditions();
         if (expeditions.isEmpty()) {
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("EXPEDITIONS_COMMAND_NONE_IN_CHANNEL").with(ch.getId()));
            continue;
         }
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("EXPEDITIONS_COMMAND_IN_CHANNEL").with(ch.getId()));
         int id = 0;
         for (MapleExpedition expedition : expeditions) {
            id++;
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("EXPEDITIONS_COMMAND_ID").with(id));
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("EXPEDITIONS_COMMAND_TYPE").with(expedition.getType().toString()));
            if (expedition.isRegistering()) {
               MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("EXPEDITIONS_COMMAND_STATUS_REGISTERING"));
            } else {
               MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("EXPEDITIONS_COMMAND_STATUS_UNDERWAY"));
            }
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("EXPEDITIONS_COMMAND_SIZE").with(expedition.getMembers().size()));
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("EXPEDITIONS_COMMAND_LEADER").with(expedition.getLeader().getName()));
            int memId = 2;

            for (Entry<Integer, String> e : expedition.getMembers().entrySet()) {
               if (expedition.isLeader(e.getKey())) {
                  continue;
               }
               MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("EXPEDITIONS_COMMAND_MEMBER").with(memId, e.getValue()));
               memId++;
            }
         }
      }
   }
}

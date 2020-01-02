package client.command.commands.gm3;

import java.util.List;
import java.util.Map.Entry;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.channel.Channel;
import server.expeditions.MapleExpedition;

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
            player.yellowMessage("No Expeditions in Channel " + ch.getId());
            continue;
         }
         player.yellowMessage("Expeditions in Channel " + ch.getId());
         int id = 0;
         for (MapleExpedition expedition : expeditions) {
            id++;
            player.yellowMessage("> Expedition " + id);
            player.yellowMessage(">> Type: " + expedition.getType().toString());
            player.yellowMessage(">> Status: " + (expedition.isRegistering() ? "REGISTERING" : "UNDERWAY"));
            player.yellowMessage(">> Size: " + expedition.getMembers().size());
            player.yellowMessage(">> Leader: " + expedition.getLeader().getName());
            int memId = 2;
            for (Entry<Integer, String> e : expedition.getMembers().entrySet()) {
               if (expedition.isLeader(e.getKey())) {
                  continue;
               }
               player.yellowMessage(">>> Member " + memId + ": " + e.getValue());
               memId++;
            }
         }
      }
   }
}

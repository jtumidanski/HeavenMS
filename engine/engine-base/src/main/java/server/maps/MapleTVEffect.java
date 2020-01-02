package server.maps;

import java.util.List;

import client.MapleCharacter;
import net.server.Server;
import server.TimerManager;
import tools.PacketCreator;
import tools.packet.EnableTV;
import tools.packet.SendTV;
import tools.packet.remove.RemoveTV;

public class MapleTVEffect {

   private final static boolean[] ACTIVE = new boolean[Server.getInstance().getWorldsSize()];

   public static synchronized boolean broadcastMapleTVIfNotActive(MapleCharacter player, MapleCharacter victim, List<String> messages, int tvType) {
      int w = player.getWorld();
      if (!ACTIVE[w]) {
         broadcastTV(true, w, messages, player, tvType, victim);
         return true;
      }

      return false;
   }

   private static synchronized void broadcastTV(boolean activity, final int userWorld, List<String> message, MapleCharacter user, int type, MapleCharacter partner) {
      Server server = Server.getInstance();
      ACTIVE[userWorld] = activity;
      if (activity) {
         server.broadcastMessage(userWorld, PacketCreator.create(new EnableTV()));
         server.broadcastMessage(userWorld, PacketCreator.create(new SendTV(user, message, type <= 2 ? type : type - 3, partner)));
         int delay = 15000;
         if (type == 4) {
            delay = 30000;
         } else if (type == 5) {
            delay = 60000;
         }
         TimerManager.getInstance().schedule(() -> broadcastTV(false, userWorld, null, null, -1, null), delay);
      } else {
         server.broadcastMessage(userWorld, PacketCreator.create(new RemoveTV()));
      }
   }
}

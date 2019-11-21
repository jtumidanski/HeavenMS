package server.processor.maps;

import client.MapleClient;
import config.YamlConfig;
import net.server.Server;

public class MapleMapProcessor {
   private static MapleMapProcessor ourInstance = new MapleMapProcessor();

   public static MapleMapProcessor getInstance() {
      return ourInstance;
   }

   private MapleMapProcessor() {
   }

   public double getCurrentSpawnRate(int numPlayers) {
      return 0.70 + (0.05 * Math.min(6, numPlayers));
   }

   public void announcePlayerDiseases(final MapleClient c) {
      Server.getInstance().registerAnnouncePlayerDiseases(c);
   }

   public double getRangedDistance() {
      return (YamlConfig.config.server.USE_MAXRANGE ? Double.POSITIVE_INFINITY : 722500);
   }
}

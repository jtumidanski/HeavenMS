package client.database.data;

import java.util.ArrayList;
import java.util.List;

public class WorldRankData {
   private int worldId;

   private List<GlobalUserRank> userRanks;

   public WorldRankData(int worldId) {
      this.worldId = worldId;
   }

   public int getWorldId() {
      return worldId;
   }

   public void addUserRank(GlobalUserRank userRank) {
      if (userRanks == null) {
         userRanks = new ArrayList<>();
      }
      userRanks.add(userRank);
   }

   public List<GlobalUserRank> getUserRanks() {
      return userRanks;
   }
}

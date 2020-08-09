package client.database.data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record WorldRankData(Integer worldId, List<GlobalUserRank> userRanks) {
   public WorldRankData(Integer worldId) {
      this(worldId, Collections.emptyList());
   }

   public WorldRankData addUserRank(GlobalUserRank userRank) {
      return new WorldRankData(worldId, Stream.concat(userRanks.stream(), Stream.of(userRank)).collect(Collectors.toUnmodifiableList()));
   }
}

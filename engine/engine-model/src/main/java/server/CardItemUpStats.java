package server;

import java.util.List;

import tools.Pair;

public record CardItemUpStats(Integer itemCode, Integer probability, List<Pair<Integer, Integer>> areas,
                              Boolean inParty) {
   public Boolean isInArea(Integer mapId) {
      if (areas == null) {
         return true;
      }
      return areas.stream().anyMatch(pair -> mapId >= pair.getLeft() && mapId <= pair.getRight());
   }
}

package server;

public record WorldRecommendation(Integer worldId, String reason) {
   @Override
   public String toString() {
      return worldId + ":" + reason;
   }
}

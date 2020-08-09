package net.server.coordinator.world;

public record PlayerAggroEntry(int cid, int averageDamage, int currentDamageInstances, long accumulatedDamage,
                               int expireStreak, int updateStreak, int toNextUpdate, int entryRank) {
   public PlayerAggroEntry(int cid) {
      this(cid, 0, 0, 0, 0, 0, 0, -1);
   }
}

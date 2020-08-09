package net.server.coordinator.world;

public class PlayerAggroEntryBuilder {
   private int cid;

   private int averageDamage;

   private int currentDamageInstances;

   private long accumulatedDamage;

   private int expireStreak;

   private int updateStreak;
   private int toNextUpdate;

   private int entryRank;

   public PlayerAggroEntryBuilder(PlayerAggroEntry other) {
      this.cid = other.cid();
      this.averageDamage = other.averageDamage();
      this.currentDamageInstances = other.currentDamageInstances();
      this.accumulatedDamage = other.accumulatedDamage();
      this.expireStreak = other.expireStreak();
      this.updateStreak = other.updateStreak();
      this.toNextUpdate = other.toNextUpdate();
      this.entryRank = other.entryRank();
   }

   public PlayerAggroEntry build() {
      return new PlayerAggroEntry(cid, averageDamage, currentDamageInstances, accumulatedDamage, expireStreak, updateStreak, toNextUpdate, entryRank);
   }

   public PlayerAggroEntryBuilder setCid(int value) {
      cid = value;
      return this;
   }

   public PlayerAggroEntryBuilder setAverageDamage(int value) {
      averageDamage = value;
      return this;
   }

   public PlayerAggroEntryBuilder setCurrentDamageInstances(int value) {
      currentDamageInstances = value;
      return this;
   }

   public PlayerAggroEntryBuilder setAccumulatedDamage(long value) {
      accumulatedDamage = value;
      return this;
   }

   public PlayerAggroEntryBuilder setExpireStreak(int value) {
      expireStreak = value;
      return this;
   }

   public PlayerAggroEntryBuilder setUpdateStreak(int value) {
      updateStreak = value;
      return this;
   }

   public PlayerAggroEntryBuilder setToNextUpdate(int value) {
      toNextUpdate = value;
      return this;
   }

   public PlayerAggroEntryBuilder setEntryRank(int value) {
      entryRank = value;
      return this;
   }
}

package client;

public record SkillEntry(Byte skillLevel, Integer masterLevel, Long expiration) {
   @Override
   public String toString() {
      return skillLevel + ":" + masterLevel;
   }
}

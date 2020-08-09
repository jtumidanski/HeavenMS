package client;

import java.util.Objects;

public record BuddyListEntry(String name, String group, int characterId, int channel, boolean visible) {
   public BuddyListEntry updateChannel(int channel) {
      return new BuddyListEntry(name, group, characterId, channel, visible);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      BuddyListEntry that = (BuddyListEntry) o;
      return Objects.equals(characterId, that.characterId);
   }

   @Override
   public int hashCode() {
      return Objects.hash(characterId);
   }
}

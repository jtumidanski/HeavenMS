package client;

import java.util.Objects;

public record Ring(int ringId, int partnerRingId, int partnerId, int itemId, String partnerName,
                   boolean equipped) implements Comparable<Ring> {
   public Ring(int ringId, int partnerRingId, int partnerId, int itemId, String partnerName) {
      this(ringId, partnerRingId, partnerId, itemId, partnerName, false);
   }

   public Ring equip() {
      return new Ring(ringId(), partnerRingId(), partnerId(), itemId(), partnerName(), true);
   }

   public Ring unequip() {
      return new Ring(ringId(), partnerRingId(), partnerId(), itemId(), partnerName(), false);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      Ring ring = (Ring) o;
      return Objects.equals(ringId, ring.ringId());
   }

   @Override
   public int hashCode() {
      return Objects.hash(ringId);
   }

   @Override
   public int compareTo(Ring o) {
      if (ringId < o.ringId()) {
         return -1;
      } else if (ringId == o.ringId()) {
         return 0;
      }
      return 1;
   }
}

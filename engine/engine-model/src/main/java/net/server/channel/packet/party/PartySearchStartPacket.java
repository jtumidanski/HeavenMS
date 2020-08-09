package net.server.channel.packet.party;

import net.server.MaplePacket;

public class PartySearchStartPacket implements MaplePacket {
   private final int min;

   private final int max;

   private final int members;

   private final int jobs;

   public PartySearchStartPacket(int min, int max, int members, int jobs) {
      this.min = min;
      this.max = max;
      this.members = members;
      this.jobs = jobs;
   }

   public int min() {
      return min;
   }

   public int max() {
      return max;
   }

   public int members() {
      return members;
   }

   public int jobs() {
      return jobs;
   }
}

package net.server.channel.packet.report;

import net.server.MaplePacket;

public class BaseReportPacket implements MaplePacket {
   private final Integer theType;

   private final String victim;

   private final Integer reason;

   private final String description;

   public BaseReportPacket(Integer theType, String victim, Integer reason, String description) {
      this.theType = theType;
      this.victim = victim;
      this.reason = reason;
      this.description = description;
   }

   public Integer theType() {
      return theType;
   }

   public String victim() {
      return victim;
   }

   public Integer reason() {
      return reason;
   }

   public String description() {
      return description;
   }
}

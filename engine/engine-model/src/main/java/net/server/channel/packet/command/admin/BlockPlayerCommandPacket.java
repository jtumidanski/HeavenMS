package net.server.channel.packet.command.admin;

public class BlockPlayerCommandPacket extends BaseAdminCommandPacket {
   private final String victim;

   private final Integer theType;

   private final Integer duration;

   private final String description;

   public BlockPlayerCommandPacket(Byte mode, String victim, Integer theType, Integer duration, String description) {
      super(mode);
      this.victim = victim;
      this.theType = theType;
      this.duration = duration;
      this.description = description;
   }

   public String victim() {
      return victim;
   }

   public Integer theType() {
      return theType;
   }

   public Integer duration() {
      return duration;
   }

   public String description() {
      return description;
   }
}

package net.server.channel.packet.npc;

public class NPCTalkPacket extends BaseNPCAnimationPacket {
   private final Integer first;

   private final Byte second;

   private final Byte third;

   public NPCTalkPacket(Integer available, Integer first, Byte second, Byte third) {
      super(available);
      this.first = first;
      this.second = second;
      this.third = third;
   }

   public Integer first() {
      return first;
   }

   public Byte second() {
      return second;
   }

   public Byte third() {
      return third;
   }
}

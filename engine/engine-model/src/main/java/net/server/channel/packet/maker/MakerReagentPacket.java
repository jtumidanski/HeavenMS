package net.server.channel.packet.maker;

public class MakerReagentPacket extends BaseMakerActionPacket {
   private final boolean isStimulant;

   private final int reagentCount;

   private final int[] reagentIds;

   public MakerReagentPacket(int theType, int toCreate, boolean isStimulant, int reagentCount, int[] reagentIds) {
      super(theType, toCreate);
      this.isStimulant = isStimulant;
      this.reagentCount = reagentCount;
      this.reagentIds = reagentIds;
   }

   public boolean isStimulant() {
      return isStimulant;
   }

   public int reagentCount() {
      return reagentCount;
   }

   public int[] reagentIds() {
      return reagentIds;
   }
}

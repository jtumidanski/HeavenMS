package net.server.channel.packet.duey;

public class DueyRemovePackagePacket extends BaseDueyPacket {
   private final Integer packageId;

   public DueyRemovePackagePacket(Byte operation, Integer packageId) {
      super(operation);
      this.packageId = packageId;
   }

   public Integer packageId() {
      return packageId;
   }
}

package net.server.channel.packet.duey;

public class DueyClaimPackagePacket extends BaseDueyPacket {
   private final Integer packageId;

   public DueyClaimPackagePacket(Byte operation, Integer packageId) {
      super(operation);
      this.packageId = packageId;
   }

   public Integer packageId() {
      return packageId;
   }
}

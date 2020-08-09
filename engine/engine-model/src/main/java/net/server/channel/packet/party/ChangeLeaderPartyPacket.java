package net.server.channel.packet.party;

public class ChangeLeaderPartyPacket extends BasePartyOperationPacket {
   private final Integer leaderId;

   public ChangeLeaderPartyPacket(Integer operation, Integer leaderId) {
      super(operation);
      this.leaderId = leaderId;
   }

   public Integer leaderId() {
      return leaderId;
   }
}

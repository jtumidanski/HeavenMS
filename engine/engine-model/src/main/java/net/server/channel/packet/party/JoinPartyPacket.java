package net.server.channel.packet.party;

public class JoinPartyPacket extends BasePartyOperationPacket {
   private final Integer partyId;

   public JoinPartyPacket(Integer operation, Integer partyId) {
      super(operation);
      this.partyId = partyId;
   }

   public Integer partyId() {
      return partyId;
   }
}

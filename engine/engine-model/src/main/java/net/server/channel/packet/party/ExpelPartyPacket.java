package net.server.channel.packet.party;

public class ExpelPartyPacket extends BasePartyOperationPacket {
   private final Integer characterId;

   public ExpelPartyPacket(Integer operation, Integer characterId) {
      super(operation);
      this.characterId = characterId;
   }

   public Integer characterId() {
      return characterId;
   }
}

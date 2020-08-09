package net.server.channel.packet.party;

public class InvitePartyPacket extends BasePartyOperationPacket {
   private final String name;

   public InvitePartyPacket(Integer operation, String name) {
      super(operation);
      this.name = name;
   }

   public String name() {
      return name;
   }
}

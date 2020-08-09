package net.server.channel.packet.interaction;

public class SetMesoPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Integer amount;

   public SetMesoPlayerInteractionPacket(Byte mode, Integer amount) {
      super(mode);
      this.amount = amount;
   }

   public Integer amount() {
      return amount;
   }
}

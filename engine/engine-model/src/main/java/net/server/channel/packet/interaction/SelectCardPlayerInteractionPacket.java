package net.server.channel.packet.interaction;

public class SelectCardPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Integer turn;

   private final Integer slot;

   public SelectCardPlayerInteractionPacket(Byte mode, Integer turn, Integer slot) {
      super(mode);
      this.turn = turn;
      this.slot = slot;
   }

   public Integer turn() {
      return turn;
   }

   public Integer slot() {
      return slot;
   }
}

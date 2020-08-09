package net.server.channel.packet.cash.operation;

public class ModifyWishListPacket extends BaseCashOperationPacket {
   private final int[] sns;

   public ModifyWishListPacket(int action, int[] sns) {
      super(action);
      this.sns = sns;
   }

   public int[] sns() {
      return sns;
   }
}

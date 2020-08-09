package net.server.channel.packet.cash.operation;

public class MesoCashItemPurchase extends BaseCashOperationPacket {
   private final Integer sn;

   public MesoCashItemPurchase(Integer action, Integer sn) {
      super(action);
      this.sn = sn;
   }

   public Integer sn() {
      return sn;
   }
}

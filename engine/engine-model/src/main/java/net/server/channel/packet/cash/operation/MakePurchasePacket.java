package net.server.channel.packet.cash.operation;

public class MakePurchasePacket extends BaseCashOperationPacket {
   private final Integer useNX;

   private final Integer snCS;

   public MakePurchasePacket(Integer action, Integer useNX, Integer snCS) {
      super(action);
      this.useNX = useNX;
      this.snCS = snCS;
   }

   public Integer useNX() {
      return useNX;
   }

   public Integer snCS() {
      return snCS;
   }
}

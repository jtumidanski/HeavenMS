package net.server.channel.packet.cash.operation;

public class CrushRingPacket extends BaseCashOperationPacket {
   private final Integer birthday;

   private final Integer toCharge;

   private final Integer sn;

   private final String recipientName;

   private final String text;

    public CrushRingPacket(Integer action, Integer birthday, Integer toCharge, Integer sn, String recipientName, String text) {
        super(action);
        this.birthday = birthday;
        this.toCharge = toCharge;
        this.sn = sn;
        this.recipientName = recipientName;
        this.text = text;
    }

    public Integer birthday() {
      return birthday;
   }

   public Integer toCharge() {
      return toCharge;
   }

   public Integer sn() {
      return sn;
   }

   public String recipientName() {
      return recipientName;
   }

   public String text() {
      return text;
   }
}

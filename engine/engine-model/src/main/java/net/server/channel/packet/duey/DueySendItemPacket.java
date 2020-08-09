package net.server.channel.packet.duey;

public class DueySendItemPacket extends BaseDueyPacket {
   private final Byte inventoryId;

   private final Short itemPosition;

   private final Short amount;

   private final Integer mesos;

   private final String recipient;

   private final Boolean quick;

   private final String message;

    public DueySendItemPacket(Byte operation, Byte inventoryId, Short itemPosition, Short amount, Integer mesos, String recipient, Boolean quick, String message) {
        super(operation);
        this.inventoryId = inventoryId;
        this.itemPosition = itemPosition;
        this.amount = amount;
        this.mesos = mesos;
        this.recipient = recipient;
        this.quick = quick;
        this.message = message;
    }

    public Byte inventoryId() {
      return inventoryId;
   }

   public Short itemPosition() {
      return itemPosition;
   }

   public Short amount() {
      return amount;
   }

   public Integer mesos() {
      return mesos;
   }

   public String recipient() {
      return recipient;
   }

   public Boolean quick() {
      return quick;
   }

   public String message() {
      return message;
   }
}

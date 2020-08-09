package net.server.channel.packet.storage;

public class TakeoutPacket extends BaseStoragePacket {
   private final Byte theType;

   private final Byte slot;

   public TakeoutPacket(Byte mode, Byte theType, Byte slot) {
      super(mode);
      this.theType = theType;
      this.slot = slot;
   }

   public Byte theType() {
      return theType;
   }

   public Byte slot() {
      return slot;
   }
}

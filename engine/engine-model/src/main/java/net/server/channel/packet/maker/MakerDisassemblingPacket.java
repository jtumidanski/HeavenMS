package net.server.channel.packet.maker;

public class MakerDisassemblingPacket extends BaseMakerActionPacket {
   private final Integer inventoryType;

   private final Integer position;

   public MakerDisassemblingPacket(Integer theType, Integer toCreate, Integer inventoryType, Integer position) {
      super(theType, toCreate);
      this.inventoryType = inventoryType;
      this.position = position;
   }

   public Integer inventoryType() {
      return inventoryType;
   }

   public Integer position() {
      return position;
   }
}

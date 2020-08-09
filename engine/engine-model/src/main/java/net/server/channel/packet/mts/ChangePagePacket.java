package net.server.channel.packet.mts;

public class ChangePagePacket extends BaseMTSPacket {
   private final Integer tab;

   private final Integer theType;

   private final Integer page;

   public ChangePagePacket(Boolean available, Byte operation, Integer tab, Integer theType, Integer page) {
      super(available, operation);
      this.tab = tab;
      this.theType = theType;
      this.page = page;
   }

   public Integer tab() {
      return tab;
   }

   public Integer theType() {
      return theType;
   }

   public Integer page() {
      return page;
   }
}

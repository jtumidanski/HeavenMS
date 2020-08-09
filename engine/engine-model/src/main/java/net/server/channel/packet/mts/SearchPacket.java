package net.server.channel.packet.mts;

public class SearchPacket extends BaseMTSPacket {
   private final Integer tab;

   private final Integer theType;

   private final Integer ci;

   private final String search;

   public SearchPacket(Boolean available, Byte operation, Integer tab, Integer theType, Integer ci, String search) {
      super(available, operation);
      this.tab = tab;
      this.theType = theType;
      this.ci = ci;
      this.search = search;
   }

   public Integer tab() {
      return tab;
   }

   public Integer theType() {
      return theType;
   }

   public Integer ci() {
      return ci;
   }

   public String search() {
      return search;
   }
}

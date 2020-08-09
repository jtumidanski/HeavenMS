package net.server.channel.packet.command.admin;

public class TestingPacket extends BaseAdminCommandPacket {
   private final int printableInt;

   public TestingPacket(byte mode, int printableInt) {
      super(mode);
      this.printableInt = printableInt;
   }

   public int printableInt() {
      return printableInt;
   }
}

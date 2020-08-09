package net.server.channel.packet.buddy;

public class AddBuddyPacket extends BaseBuddyPacket {
   private final String name;

   private final String group;

   public AddBuddyPacket(Integer mode, String name, String group) {
      super(mode);
      this.name = name;
      this.group = group;
   }

   public String name() {
      return name;
   }

   public String group() {
      return group;
   }
}

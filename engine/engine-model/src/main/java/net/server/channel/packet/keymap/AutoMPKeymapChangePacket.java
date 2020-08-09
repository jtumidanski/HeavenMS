package net.server.channel.packet.keymap;

public class AutoMPKeymapChangePacket extends BaseKeymapChangePacket {
   private final Integer itemId;

   public AutoMPKeymapChangePacket(Boolean available, Integer mode, Integer itemId) {
      super(available, mode);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}

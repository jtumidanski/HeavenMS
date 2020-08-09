package net.server.channel.packet.keymap;

public class AutoHPKeymapChangePacket extends BaseKeymapChangePacket {
   private final Integer itemId;

   public AutoHPKeymapChangePacket(Boolean available, Integer mode, Integer itemId) {
      super(available, mode);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}

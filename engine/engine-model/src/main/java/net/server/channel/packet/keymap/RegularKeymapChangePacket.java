package net.server.channel.packet.keymap;

public class RegularKeymapChangePacket extends BaseKeymapChangePacket {
   private final KeyTypeAction[] changes;

   public RegularKeymapChangePacket(Boolean available, Integer mode, KeyTypeAction[] changes) {
      super(available, mode);
      this.changes = changes;
   }

   public KeyTypeAction[] changes() {
      return changes;
   }
}

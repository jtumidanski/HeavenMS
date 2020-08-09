package net.server.channel.packet;

import net.server.MaplePacket;

public class ClearNotePacket extends BaseNoteActionPacket implements MaplePacket {
   private final int[] ids;

   public ClearNotePacket(Integer action, int[] ids) {
      super(action);
      this.ids = ids;
   }

   public int[] ids() {
      return ids;
   }
}

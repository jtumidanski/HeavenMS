package tools.packet.remove;

import net.opcodes.SendOpcode;
import server.maps.MapleSummon;
import tools.packet.PacketInput;

public class RemoveSummon implements PacketInput {
   private MapleSummon summon;

   private boolean animated;

   public RemoveSummon(MapleSummon summon, boolean animated) {
      this.summon = summon;
      this.animated = animated;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_SPECIAL_MAPOBJECT;
   }

   public MapleSummon getSummon() {
      return summon;
   }

   public boolean isAnimated() {
      return animated;
   }
}

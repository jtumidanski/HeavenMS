package net.server.channel.packet.npc;

public class NPCMovePacket extends BaseNPCAnimationPacket {
   private final byte[] movement;

   public NPCMovePacket(int available, byte[] movement) {
      super(available);
      this.movement = movement;
   }

   public byte[] movement() {
      return movement;
   }
}

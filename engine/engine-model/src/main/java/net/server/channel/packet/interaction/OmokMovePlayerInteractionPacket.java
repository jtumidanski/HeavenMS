package net.server.channel.packet.interaction;

public class OmokMovePlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Integer x;

   private final Integer y;

   private final Integer theType;

   public OmokMovePlayerInteractionPacket(Byte mode, Integer x, Integer y, Integer theType) {
      super(mode);
      this.x = x;
      this.y = y;
      this.theType = theType;
   }

   public Integer x() {
      return x;
   }

   public Integer y() {
      return y;
   }

   public Integer theType() {
      return theType;
   }
}

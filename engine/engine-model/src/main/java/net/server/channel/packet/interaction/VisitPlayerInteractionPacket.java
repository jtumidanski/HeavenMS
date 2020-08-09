package net.server.channel.packet.interaction;

public class VisitPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Integer objectId;

   private final String password;

   public VisitPlayerInteractionPacket(Byte mode, Integer objectId, String password) {
      super(mode);
      this.objectId = objectId;
      this.password = password;
   }

   public VisitPlayerInteractionPacket(Byte mode, Integer objectId) {
      super(mode);
      this.objectId = objectId;
      this.password = "";
   }

   public Integer objectId() {
      return objectId;
   }

   public String password() {
      return password;
   }
}

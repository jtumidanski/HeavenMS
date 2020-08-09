package net.server.channel.packet.command.admin;

public class ChangeMapPacket extends BaseAdminCommandPacket {
   private final String victim;

   private final Integer mapId;

   public ChangeMapPacket(Byte mode, String victim, Integer mapId) {
      super(mode);
      this.victim = victim;
      this.mapId = mapId;
   }

   public String victim() {
      return victim;
   }

   public Integer mapId() {
      return mapId;
   }
}

package net.server.channel.packet.cash.use;

public class UseTeleportRockPacket extends AbstractUseCashItemPacket {
   private final Boolean vip;

   private final Integer mapId;

   private final String name;

   public UseTeleportRockPacket(Short position, Integer itemId, Boolean vip, Integer mapId, String name) {
      super(position, itemId);
      this.vip = vip;
      this.mapId = mapId;
      this.name = name;
   }

   public Boolean vip() {
      return vip;
   }

   public Integer mapId() {
      return mapId;
   }

   public String name() {
      return name;
   }
}

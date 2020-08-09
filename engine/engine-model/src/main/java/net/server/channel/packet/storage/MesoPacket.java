package net.server.channel.packet.storage;

public class MesoPacket extends BaseStoragePacket {
   private final Integer mesos;

   public MesoPacket(Byte mode, Integer mesos) {
      super(mode);
      this.mesos = mesos;
   }

   public Integer mesos() {
      return mesos;
   }
}

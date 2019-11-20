package tools.packet.factory;

import java.awt.Point;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.reactor.DestroyReactor;
import tools.packet.reactor.SpawnReactor;
import tools.packet.reactor.TriggerReactor;

public class ReactorPacketFactory extends AbstractPacketFactory {
   private static ReactorPacketFactory instance;

   public static ReactorPacketFactory getInstance() {
      if (instance == null) {
         instance = new ReactorPacketFactory();
      }
      return instance;
   }

   private ReactorPacketFactory() {
      Handler.handle(SpawnReactor.class).decorate(this::spawnReactor).register(registry);
      Handler.handle(TriggerReactor.class).decorate(this::triggerReactor).register(registry);
      Handler.handle(DestroyReactor.class).decorate(this::destroyReactor).register(registry);
   }

   // is there a way to spawn reactors non-animated?
   protected void spawnReactor(MaplePacketLittleEndianWriter writer, SpawnReactor packet) {
      Point pos = packet.getReactor().position();
      writer.writeInt(packet.getReactor().objectId());
      writer.writeInt(packet.getReactor().getId());
      writer.write(packet.getReactor().getState());
      writer.writePos(pos);
      writer.write(0);
      writer.writeShort(0);
   }

   // is there a way to trigger reactors without performing the hit animation?
   protected void triggerReactor(MaplePacketLittleEndianWriter writer, TriggerReactor packet) {
      Point pos = packet.getReactor().position();
      writer.writeInt(packet.getReactor().objectId());
      writer.write(packet.getReactor().getState());
      writer.writePos(pos);
      writer.write(packet.getStance());
      writer.writeShort(0);
      writer.write(5); // frame delay, set to 5 since there doesn't appear to be a fixed formula for it
   }

   protected void destroyReactor(MaplePacketLittleEndianWriter writer, DestroyReactor packet) {
      Point pos = packet.getReactor().position();
      writer.writeInt(packet.getReactor().objectId());
      writer.write(packet.getReactor().getState());
      writer.writePos(pos);
   }
}
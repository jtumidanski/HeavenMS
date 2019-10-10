package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.SelectWorld;

public class SelectWorldPacketFactory extends AbstractPacketFactory {
   private static SelectWorldPacketFactory instance;

   public static SelectWorldPacketFactory getInstance() {
      if (instance == null) {
         instance = new SelectWorldPacketFactory();
      }
      return instance;
   }

   private SelectWorldPacketFactory() {
      registry.setHandler(SelectWorld.class, packet -> create(SendOpcode.LAST_CONNECTED_WORLD, this::selectWorld, packet));
   }

   protected void selectWorld(MaplePacketLittleEndianWriter writer, SelectWorld packet) {
      writer.writeInt(packet.worldId());//According to GMS, it should be the world that contains the most characters (most active)
   }
}
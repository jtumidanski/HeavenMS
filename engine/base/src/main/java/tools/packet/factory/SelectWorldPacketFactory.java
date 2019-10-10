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
      registry.setHandler(SelectWorld.class, packet -> this.selectWorld((SelectWorld) packet));
   }

   protected byte[] selectWorld(SelectWorld packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.LAST_CONNECTED_WORLD.getValue());
      mplew.writeInt(packet.worldId());//According to GMS, it should be the world that contains the most characters (most active)
      return mplew.getPacket();
   }
}
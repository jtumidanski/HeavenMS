package tools.packet.factory;

import java.awt.Point;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.factory.AbstractPacketFactory;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof SpawnReactor) {
         return create(this::spawnReactor, packetInput);
      } else if (packetInput instanceof TriggerReactor) {
         return create(this::triggerReactor, packetInput);
      } else if (packetInput instanceof DestroyReactor) {
         return create(this::destroyReactor, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   // is there a way to spawn reactors non-animated?
   protected byte[] spawnReactor(SpawnReactor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      Point pos = packet.getReactor().getPosition();
      mplew.writeShort(SendOpcode.REACTOR_SPAWN.getValue());
      mplew.writeInt(packet.getReactor().getObjectId());
      mplew.writeInt(packet.getReactor().getId());
      mplew.write(packet.getReactor().getState());
      mplew.writePos(pos);
      mplew.write(0);
      mplew.writeShort(0);
      return mplew.getPacket();
   }

   // is there a way to trigger reactors without performing the hit animation?
   protected byte[] triggerReactor(TriggerReactor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      Point pos = packet.getReactor().getPosition();
      mplew.writeShort(SendOpcode.REACTOR_HIT.getValue());
      mplew.writeInt(packet.getReactor().getObjectId());
      mplew.write(packet.getReactor().getState());
      mplew.writePos(pos);
      mplew.write(packet.getStance());
      mplew.writeShort(0);
      mplew.write(5); // frame delay, set to 5 since there doesn't appear to be a fixed formula for it
      return mplew.getPacket();
   }

   protected byte[] destroyReactor(DestroyReactor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      Point pos = packet.getReactor().getPosition();
      mplew.writeShort(SendOpcode.REACTOR_DESTROY.getValue());
      mplew.writeInt(packet.getReactor().getObjectId());
      mplew.write(packet.getReactor().getState());
      mplew.writePos(pos);
      return mplew.getPacket();
   }
}
package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.DojoWarpUp;
import tools.packet.EnableReport;
import tools.packet.GetEnergy;
import tools.packet.PacketInput;
import tools.packet.SetNPCScriptable;
import tools.packet.factory.AbstractPacketFactory;

public class GenericPacketFactory extends AbstractPacketFactory {
   private static GenericPacketFactory instance;

   public static GenericPacketFactory getInstance() {
      if (instance == null) {
         instance = new GenericPacketFactory();
      }
      return instance;
   }

   private GenericPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof EnableReport) {
         return create(this::enableReport, packetInput);
      } else if (packetInput instanceof GetEnergy) {
         return create(this::getEnergy, packetInput);
      } else if (packetInput instanceof DojoWarpUp) {
         return create(this::dojoWarpUp, packetInput);
      } else if (packetInput instanceof SetNPCScriptable) {
         return create(this::setNPCScriptable, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] enableReport(EnableReport packet) { // thanks to snow
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.CLAIM_STATUS_CHANGED.getValue());
      mplew.write(1);
      return mplew.getPacket();
   }

   protected byte[] getEnergy(GetEnergy packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SESSION_VALUE.getValue());
      mplew.writeMapleAsciiString(packet.info());
      mplew.writeMapleAsciiString(Integer.toString(packet.amount()));
      return mplew.getPacket();
   }

   protected byte[] dojoWarpUp(DojoWarpUp packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DOJO_WARP_UP.getValue());
      mplew.write(0);
      mplew.write(6);
      return mplew.getPacket();
   }

   protected byte[] setNPCScriptable(SetNPCScriptable packet) {  // thanks to GabrielSin
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_NPC_SCRIPTABLE.getValue());
      mplew.write(packet.descriptions().size());
      for (Pair<Integer, String> p : packet.descriptions()) {
         mplew.writeInt(p.getLeft());
         mplew.writeMapleAsciiString(p.getRight());
         mplew.writeInt(0); // start time
         mplew.writeInt(Integer.MAX_VALUE); // end time
      }
      return mplew.getPacket();
   }
}
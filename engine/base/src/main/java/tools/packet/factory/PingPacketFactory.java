package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.Ping;

public class PingPacketFactory extends AbstractPacketFactory {
   private static PingPacketFactory instance;

   public static PingPacketFactory getInstance() {
      if (instance == null) {
         instance = new PingPacketFactory();
      }
      return instance;
   }

   private PingPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof Ping) {
         create(this::getPing, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Sends a ping packet.
    *
    * @return The packet.
    */
   protected byte[] getPing(Ping packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
      mplew.writeShort(SendOpcode.PING.getValue());
      return mplew.getPacket();
   }
}
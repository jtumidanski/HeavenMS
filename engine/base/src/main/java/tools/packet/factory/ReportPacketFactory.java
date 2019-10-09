package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.factory.AbstractPacketFactory;
import tools.packet.report.ReportResponse;
import tools.packet.report.SendPolice;

public class ReportPacketFactory extends AbstractPacketFactory {
   private static ReportPacketFactory instance;

   public static ReportPacketFactory getInstance() {
      if (instance == null) {
         instance = new ReportPacketFactory();
      }
      return instance;
   }

   private ReportPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ReportResponse) {
         return create(this::reportResponse, packetInput);
      } else if (packetInput instanceof SendPolice) {
         return create(this::sendPolice, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Sends a report response
    * <p>
    * Possible values for <code>mode</code>:<br> 0: You have succesfully
    * reported the user.<br> 1: Unable to locate the user.<br> 2: You may only
    * report users 10 times a day.<br> 3: You have been reported to the GM's by
    * a user.<br> 4: Your request did not go through for unknown reasons.
    * Please try again later.<br>
    */
   protected byte[] reportResponse(ReportResponse packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SUE_CHARACTER_RESULT.getValue());
      mplew.write(packet.mode());
      return mplew.getPacket();
   }

   protected byte[] sendPolice(SendPolice packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DATA_CRC_CHECK_FAILED.getValue());
      mplew.writeMapleAsciiString(packet.text());
      return mplew.getPacket();
   }
}
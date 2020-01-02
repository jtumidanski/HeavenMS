package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
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
      Handler.handle(ReportResponse.class).decorate(this::reportResponse).register(registry);
      Handler.handle(SendPolice.class).decorate(this::sendPolice).register(registry);
   }

   /**
    * Sends a report response
    * <p>
    * Possible values for <code>mode</code>:<br> 0: You have successfully
    * reported the user.<br> 1: Unable to locate the user.<br> 2: You may only
    * report users 10 times a day.<br> 3: You have been reported to the GM's by
    * a user.<br> 4: Your request did not go through for unknown reasons.
    * Please try again later.<br>
    */
   protected void reportResponse(MaplePacketLittleEndianWriter writer, ReportResponse packet) {
      writer.write(packet.mode());
   }

   protected void sendPolice(MaplePacketLittleEndianWriter writer, SendPolice packet) {
      writer.writeMapleAsciiString(packet.text());
   }
}
package tools.packet.factory;

import tools.FilePrinter;
import tools.packet.PacketInput;
import tools.packet.factory.AbstractPacketFactory;

public class AllianceOperationPacketFactory extends AbstractPacketFactory {
   private static AllianceOperationPacketFactory instance;

   public static AllianceOperationPacketFactory getInstance() {
      if (instance == null) {
         instance = new AllianceOperationPacketFactory();
      }
      return instance;
   }

   private AllianceOperationPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }
}
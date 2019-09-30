package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.AddNewCharacter;
import tools.packet.PacketInput;

public class AddNewCharacterPacketFactory extends AbstractPacketFactory {
   private static AddNewCharacterPacketFactory instance;

   public static AddNewCharacterPacketFactory getInstance() {
      if (instance == null) {
         instance = new AddNewCharacterPacketFactory();
      }
      return instance;
   }

   private AddNewCharacterPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof AddNewCharacter) {
         return create(this::addNewCharEntry, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] addNewCharEntry(AddNewCharacter packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ADD_NEW_CHAR_ENTRY.getValue());
      mplew.write(0);
      addCharEntry(mplew, packet.getMapleCharacter(), false);
      return mplew.getPacket();
   }
}
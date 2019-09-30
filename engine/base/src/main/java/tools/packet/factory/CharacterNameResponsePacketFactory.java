package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.CharacterName;
import tools.packet.PacketInput;

public class CharacterNameResponsePacketFactory extends AbstractPacketFactory {
   private static CharacterNameResponsePacketFactory instance;

   public static CharacterNameResponsePacketFactory getInstance() {
      if (instance == null) {
         instance = new CharacterNameResponsePacketFactory();
      }
      return instance;
   }

   private CharacterNameResponsePacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof CharacterName) {
         return create(this::charNameResponse, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] charNameResponse(CharacterName packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CHAR_NAME_RESPONSE.getValue());
      mplew.writeMapleAsciiString(packet.characterName());
      mplew.write(packet.nameUsed() ? 1 : 0);
      return mplew.getPacket();
   }
}
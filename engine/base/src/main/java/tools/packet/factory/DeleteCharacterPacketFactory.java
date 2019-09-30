package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.DeleteCharacter;
import tools.packet.PacketInput;

public class DeleteCharacterPacketFactory extends AbstractPacketFactory {
   private static DeleteCharacterPacketFactory instance;

   public static DeleteCharacterPacketFactory getInstance() {
      if (instance == null) {
         instance = new DeleteCharacterPacketFactory();
      }
      return instance;
   }

   private DeleteCharacterPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof DeleteCharacter) {
         return create(this::deleteCharResponse, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] deleteCharResponse(DeleteCharacter packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DELETE_CHAR_RESPONSE.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.state().getValue());
      return mplew.getPacket();
   }
}
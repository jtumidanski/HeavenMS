package tools.packet.factory;

import java.util.List;

import client.MapleCharacter;
import constants.ServerConstants;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.character.CharacterList;
import tools.packet.PacketInput;

public class CharacterListPacketFactory extends AbstractPacketFactory {
   private static CharacterListPacketFactory instance;

   public static CharacterListPacketFactory getInstance() {
      if (instance == null) {
         instance = new CharacterListPacketFactory();
      }
      return instance;
   }

   private CharacterListPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof CharacterList) {
         return create(this::getCharList, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Gets a packet with a list of characters.
    *
    * @return The character list packet.
    */
   protected byte[] getCharList(CharacterList packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CHARLIST.getValue());
      mplew.write(packet.getStatus());
      List<MapleCharacter> chars = packet.getClient().loadCharacters(packet.getServerId());
      mplew.write((byte) chars.size());
      for (MapleCharacter chr : chars) {
         addCharEntry(mplew, chr, false);
      }

      mplew.write(ServerConstants.ENABLE_PIC && packet.getClient().cannotBypassPic() ? (packet.getClient().getPic() == null || packet.getClient().getPic().equals("") ? 0 : 1) : 2);
      mplew.writeInt(ServerConstants.COLLECTIVE_CHARSLOT ? chars.size() + packet.getClient().getAvailableCharacterSlots() : packet.getClient().getCharacterSlots());
      return mplew.getPacket();
   }
}
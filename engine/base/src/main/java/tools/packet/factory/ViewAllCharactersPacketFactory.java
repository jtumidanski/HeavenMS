package tools.packet.factory;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.ShowAllCharacter;
import tools.packet.ShowAllCharacterInfo;

public class ViewAllCharactersPacketFactory extends AbstractPacketFactory {
   private static ViewAllCharactersPacketFactory instance;

   public static ViewAllCharactersPacketFactory getInstance() {
      if (instance == null) {
         instance = new ViewAllCharactersPacketFactory();
      }
      return instance;
   }

   private ViewAllCharactersPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ShowAllCharacter) {
         return create(this::showAllCharacter, packetInput);
      } else if (packetInput instanceof ShowAllCharacterInfo) {
         return create(this::showAllCharacterInfo, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] showAllCharacter(ShowAllCharacter packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(11);
      mplew.writeShort(SendOpcode.VIEW_ALL_CHAR.getValue());
      mplew.write(packet.chars() > 0 ? 1 : 5); // 2: already connected to server, 3 : unk error (view-all-characters), 5 : cannot find any
      mplew.writeInt(packet.chars());
      mplew.writeInt(packet.unk());
      return mplew.getPacket();
   }

   protected byte[] showAllCharacterInfo(ShowAllCharacterInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.VIEW_ALL_CHAR.getValue());
      mplew.write(0);
      mplew.write(packet.getWorldId());
      mplew.write(packet.getCharacterList().size());
      for (MapleCharacter chr : packet.getCharacterList()) {
         addCharEntry(mplew, chr, true);
      }
      mplew.write(packet.isUsePic() ? 1 : 2);
      return mplew.getPacket();
   }
}
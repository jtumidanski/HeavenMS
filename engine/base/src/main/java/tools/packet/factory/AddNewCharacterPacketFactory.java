package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.AddNewCharacter;

public class AddNewCharacterPacketFactory extends AbstractPacketFactory {
   private static AddNewCharacterPacketFactory instance;

   public static AddNewCharacterPacketFactory getInstance() {
      if (instance == null) {
         instance = new AddNewCharacterPacketFactory();
      }
      return instance;
   }

   private AddNewCharacterPacketFactory() {
      registry.setHandler(AddNewCharacter.class, packet -> this.addNewCharEntry((AddNewCharacter) packet));
   }

   protected byte[] addNewCharEntry(AddNewCharacter packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ADD_NEW_CHAR_ENTRY.getValue());
      mplew.write(0);
      addCharEntry(mplew, packet.getMapleCharacter(), false);
      return mplew.getPacket();
   }
}
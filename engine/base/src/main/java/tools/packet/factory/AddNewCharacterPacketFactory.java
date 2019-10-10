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
      registry.setHandler(AddNewCharacter.class, packet -> create(SendOpcode.ADD_NEW_CHAR_ENTRY, this::addNewCharEntry, packet));
   }

   protected void addNewCharEntry(MaplePacketLittleEndianWriter writer, AddNewCharacter packet) {
      writer.write(0);
      addCharEntry(writer, packet.getMapleCharacter(), false);
   }
}
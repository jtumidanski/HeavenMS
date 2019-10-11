package tools.packet.factory;

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
      Handler.handle(AddNewCharacter.class).decorate(this::addNewCharEntry).register(registry);
   }

   protected void addNewCharEntry(MaplePacketLittleEndianWriter writer, AddNewCharacter packet) {
      writer.write(0);
      addCharEntry(writer, packet.getMapleCharacter(), false);
   }
}
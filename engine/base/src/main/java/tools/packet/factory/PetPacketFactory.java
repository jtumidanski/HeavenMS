package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.pet.PetChat;
import tools.packet.pet.PetCommandResponse;
import tools.packet.pet.PetExceptionList;
import tools.packet.pet.PetFoodResponse;
import tools.packet.pet.PetNameChange;

public class PetPacketFactory extends AbstractPacketFactory {
   private static PetPacketFactory instance;

   public static PetPacketFactory getInstance() {
      if (instance == null) {
         instance = new PetPacketFactory();
      }
      return instance;
   }

   private PetPacketFactory() {
      Handler.handle(PetChat.class).decorate(this::petChat).register(registry);
      Handler.handle(PetFoodResponse.class).decorate(this::petFoodResponse).register(registry);
      Handler.handle(PetCommandResponse.class).decorate(this::commandResponse).register(registry);
      Handler.handle(PetNameChange.class).decorate(this::changePetName).register(registry);
      Handler.handle(PetExceptionList.class).decorate(this::loadExceptionList).register(registry);
   }

   protected void petChat(MaplePacketLittleEndianWriter writer, PetChat packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.index());
      writer.write(0);
      writer.write(packet.act());
      writer.writeMapleAsciiString(packet.text());
      writer.write(0);
   }

   protected void petFoodResponse(MaplePacketLittleEndianWriter writer, PetFoodResponse packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.index());
      writer.write(1);
      writer.writeBool(packet.success());
      writer.writeBool(packet.balloonType());
   }

   protected void commandResponse(MaplePacketLittleEndianWriter writer, PetCommandResponse packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.index());
      writer.write(0);
      writer.write(packet.animation());
      writer.writeBool(!packet.talk());
      writer.writeBool(packet.balloonType());
   }

   protected void changePetName(MaplePacketLittleEndianWriter writer, PetNameChange packet) {
      writer.writeInt(packet.characterId());
      writer.write(0);
      writer.writeMapleAsciiString(packet.newName());
      writer.write(0);
   }

   protected void loadExceptionList(MaplePacketLittleEndianWriter writer, PetExceptionList packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.petIndex());
      writer.writeLong(packet.petId());
      writer.write(packet.exclusionList().size());
      for (final Integer ids : packet.exclusionList()) {
         writer.writeInt(ids);
      }
   }
}
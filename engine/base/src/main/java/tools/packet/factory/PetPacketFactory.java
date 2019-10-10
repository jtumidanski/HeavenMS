package tools.packet.factory;

import net.opcodes.SendOpcode;
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
      registry.setHandler(PetChat.class, packet -> create(SendOpcode.PET_CHAT, this::petChat, packet));
      registry.setHandler(PetFoodResponse.class, packet -> create(SendOpcode.PET_COMMAND, this::petFoodResponse, packet));
      registry.setHandler(PetCommandResponse.class, packet -> create(SendOpcode.PET_COMMAND, this::commandResponse, packet));
      registry.setHandler(PetNameChange.class, packet -> create(SendOpcode.PET_NAMECHANGE, this::changePetName, packet));
      registry.setHandler(PetExceptionList.class, packet -> create(SendOpcode.PET_EXCEPTION_LIST, this::loadExceptionList, packet));
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
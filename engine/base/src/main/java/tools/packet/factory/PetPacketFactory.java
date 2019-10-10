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
      registry.setHandler(PetChat.class, packet -> this.petChat((PetChat) packet));
      registry.setHandler(PetFoodResponse.class, packet -> this.petFoodResponse((PetFoodResponse) packet));
      registry.setHandler(PetCommandResponse.class, packet -> this.commandResponse((PetCommandResponse) packet));
      registry.setHandler(PetNameChange.class, packet -> this.changePetName((PetNameChange) packet));
      registry.setHandler(PetExceptionList.class, packet -> this.loadExceptionList((PetExceptionList) packet));
   }

   protected byte[] petChat(PetChat packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PET_CHAT.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.index());
      mplew.write(0);
      mplew.write(packet.act());
      mplew.writeMapleAsciiString(packet.text());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] petFoodResponse(PetFoodResponse packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PET_COMMAND.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.index());
      mplew.write(1);
      mplew.writeBool(packet.success());
      mplew.writeBool(packet.balloonType());
      return mplew.getPacket();
   }

   protected byte[] commandResponse(PetCommandResponse packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PET_COMMAND.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.index());
      mplew.write(0);
      mplew.write(packet.animation());
      mplew.writeBool(!packet.talk());
      mplew.writeBool(packet.balloonType());
      return mplew.getPacket();
   }

   protected byte[] changePetName(PetNameChange packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PET_NAMECHANGE.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(0);
      mplew.writeMapleAsciiString(packet.newName());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] loadExceptionList(PetExceptionList packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PET_EXCEPTION_LIST.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.petIndex());
      mplew.writeLong(packet.petId());
      mplew.write(packet.exclusionList().size());
      for (final Integer ids : packet.exclusionList()) {
         mplew.writeInt(ids);
      }
      return mplew.getPacket();
   }
}
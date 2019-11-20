package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.pet.PetChatPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PetChatReader implements PacketReader<PetChatPacket> {
   @Override
   public PetChatPacket read(SeekableLittleEndianAccessor accessor) {
      int petId = accessor.readInt();
      accessor.readInt();
      accessor.readByte();
      int act = accessor.readByte();
      String text = accessor.readMapleAsciiString();
      return new PetChatPacket(petId, act, text);
   }
}

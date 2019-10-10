package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.CharacterName;

public class CharacterNameResponsePacketFactory extends AbstractPacketFactory {
   private static CharacterNameResponsePacketFactory instance;

   public static CharacterNameResponsePacketFactory getInstance() {
      if (instance == null) {
         instance = new CharacterNameResponsePacketFactory();
      }
      return instance;
   }

   private CharacterNameResponsePacketFactory() {
      registry.setHandler(CharacterName.class, packet -> this.charNameResponse((CharacterName) packet));
   }

   protected byte[] charNameResponse(CharacterName packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CHAR_NAME_RESPONSE.getValue());
      mplew.writeMapleAsciiString(packet.characterName());
      mplew.write(packet.nameUsed() ? 1 : 0);
      return mplew.getPacket();
   }
}
package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.CharacterLook;
import tools.packet.PacketInput;

public class CharacterPacketFactory extends AbstractPacketFactory {
   private static CharacterPacketFactory instance;

   public static CharacterPacketFactory getInstance() {
      if (instance == null) {
         instance = new CharacterPacketFactory();
      }
      return instance;
   }

   private CharacterPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof CharacterLook) {
         return create(this::updateCharLook, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] updateCharLook(CharacterLook packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_CHAR_LOOK.getValue());
      mplew.writeInt(packet.getReference().getId());
      mplew.write(1);
      addCharLook(mplew, packet.getReference(), false);
      addRingLook(mplew, packet.getReference(), true);
      addRingLook(mplew, packet.getReference(), false);
      addMarriageRingLook(packet.getTarget(), mplew, packet.getReference());
      mplew.writeInt(0);
      return mplew.getPacket();
   }
}
package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.EnableTV;
import tools.packet.SendTV;

public class TVPacketFactory extends AbstractPacketFactory {
   private static TVPacketFactory instance;

   public static TVPacketFactory getInstance() {
      if (instance == null) {
         instance = new TVPacketFactory();
      }
      return instance;
   }

   private TVPacketFactory() {
      registry.setHandler(EnableTV.class, packet -> this.enableTV((EnableTV) packet));
      registry.setHandler(SendTV.class, packet -> this.sendTV((SendTV) packet));
   }

   protected byte[] enableTV(EnableTV packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.ENABLE_TV.getValue());
      mplew.writeInt(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   /**
    * Sends MapleTV
    *
    * @return the SEND_TV packet
    */
   protected byte[] sendTV(SendTV packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SEND_TV.getValue());
      mplew.write(packet.getPartner() != null ? 3 : 1);
      mplew.write(packet.getType()); //Heart = 2  Star = 1  Normal = 0
      addCharLook(mplew, packet.getCharacter(), false);
      mplew.writeMapleAsciiString(packet.getCharacter().getName());
      if (packet.getPartner() != null) {
         mplew.writeMapleAsciiString(packet.getPartner().getName());
      } else {
         mplew.writeShort(0);
      }
      for (int i = 0; i < packet.getMessages().size(); i++) {
         if (i == 4 && packet.getMessages().get(4).length() > 15) {
            mplew.writeMapleAsciiString(packet.getMessages().get(4).substring(0, 15));
         } else {
            mplew.writeMapleAsciiString(packet.getMessages().get(i));
         }
      }
      mplew.writeInt(1337); // time limit shit lol 'Your thing still start in blah blah seconds'
      if (packet.getPartner() != null) {
         addCharLook(mplew, packet.getPartner(), false);
      }
      return mplew.getPacket();
   }
}
package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.messenger.MessengerAddCharacter;
import tools.packet.messenger.MessengerChat;
import tools.packet.messenger.MessengerInvite;
import tools.packet.messenger.MessengerJoin;
import tools.packet.messenger.MessengerNote;
import tools.packet.messenger.MessengerRemoveCharacter;
import tools.packet.messenger.MessengerUpdateCharacter;

public class MessengerPacketFactory extends AbstractPacketFactory {
   private static MessengerPacketFactory instance;

   public static MessengerPacketFactory getInstance() {
      if (instance == null) {
         instance = new MessengerPacketFactory();
      }
      return instance;
   }

   private MessengerPacketFactory() {
      registry.setHandler(MessengerInvite.class, packet -> this.messengerInvite((MessengerInvite) packet));
      registry.setHandler(MessengerAddCharacter.class, packet -> this.addMessengerPlayer((MessengerAddCharacter) packet));
      registry.setHandler(MessengerRemoveCharacter.class, packet -> this.removeMessengerPlayer((MessengerRemoveCharacter) packet));
      registry.setHandler(MessengerUpdateCharacter.class, packet -> this.updateMessengerPlayer((MessengerUpdateCharacter) packet));
      registry.setHandler(MessengerJoin.class, packet -> this.joinMessenger((MessengerJoin) packet));
      registry.setHandler(MessengerChat.class, packet -> this.messengerChat((MessengerChat) packet));
      registry.setHandler(MessengerNote.class, packet -> this.messengerNote((MessengerNote) packet));
   }

   protected byte[] messengerInvite(MessengerInvite packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MESSENGER.getValue());
      mplew.write(0x03);
      mplew.writeMapleAsciiString(packet.characterNameFrom());
      mplew.write(0);
      mplew.writeInt(packet.messengerId());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] addMessengerPlayer(MessengerAddCharacter packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MESSENGER.getValue());
      mplew.write(0x00);
      mplew.write(packet.getPosition());
      addCharLook(mplew, packet.getCharacter(), true);
      mplew.writeMapleAsciiString(packet.getCharacterNameFrom());
      mplew.write(packet.getChannel());
      mplew.write(0x00);
      return mplew.getPacket();
   }

   protected byte[] removeMessengerPlayer(MessengerRemoveCharacter packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MESSENGER.getValue());
      mplew.write(0x02);
      mplew.write(packet.position());
      return mplew.getPacket();
   }

   protected byte[] updateMessengerPlayer(MessengerUpdateCharacter packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MESSENGER.getValue());
      mplew.write(0x07);
      mplew.write(packet.getPosition());
      addCharLook(mplew, packet.getCharacter(), true);
      mplew.writeMapleAsciiString(packet.getCharacterNameFrom());
      mplew.write(packet.getChannel());
      mplew.write(0x00);
      return mplew.getPacket();
   }

   protected byte[] joinMessenger(MessengerJoin packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MESSENGER.getValue());
      mplew.write(0x01);
      mplew.write(packet.position());
      return mplew.getPacket();
   }

   protected byte[] messengerChat(MessengerChat packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MESSENGER.getValue());
      mplew.write(0x06);
      mplew.writeMapleAsciiString(packet.text());
      return mplew.getPacket();
   }

   protected byte[] messengerNote(MessengerNote packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MESSENGER.getValue());
      mplew.write(packet.mode());
      mplew.writeMapleAsciiString(packet.text());
      mplew.write(packet.mode2());
      return mplew.getPacket();
   }
}
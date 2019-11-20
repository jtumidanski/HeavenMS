package tools.packet.factory;

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
      Handler.handle(MessengerInvite.class).decorate(this::messengerInvite).register(registry);
      Handler.handle(MessengerAddCharacter.class).decorate(this::addMessengerPlayer).register(registry);
      Handler.handle(MessengerRemoveCharacter.class).decorate(this::removeMessengerPlayer).register(registry);
      Handler.handle(MessengerUpdateCharacter.class).decorate(this::updateMessengerPlayer).register(registry);
      Handler.handle(MessengerJoin.class).decorate(this::joinMessenger).register(registry);
      Handler.handle(MessengerChat.class).decorate(this::messengerChat).register(registry);
      Handler.handle(MessengerNote.class).decorate(this::messengerNote).register(registry);
   }

   protected void messengerInvite(MaplePacketLittleEndianWriter writer, MessengerInvite packet) {
      writer.write(0x03);
      writer.writeMapleAsciiString(packet.characterNameFrom());
      writer.write(0);
      writer.writeInt(packet.messengerId());
      writer.write(0);
   }

   protected void addMessengerPlayer(MaplePacketLittleEndianWriter writer, MessengerAddCharacter packet) {
      writer.write(0x00);
      writer.write(packet.getPosition());
      addCharLook(writer, packet.getCharacter(), true);
      writer.writeMapleAsciiString(packet.getCharacterNameFrom());
      writer.write(packet.getChannel());
      writer.write(0x00);
   }

   protected void removeMessengerPlayer(MaplePacketLittleEndianWriter writer, MessengerRemoveCharacter packet) {
      writer.write(0x02);
      writer.write(packet.position());
   }

   protected void updateMessengerPlayer(MaplePacketLittleEndianWriter writer, MessengerUpdateCharacter packet) {
      writer.write(0x07);
      writer.write(packet.getPosition());
      addCharLook(writer, packet.getCharacter(), true);
      writer.writeMapleAsciiString(packet.getCharacterNameFrom());
      writer.write(packet.getChannel());
      writer.write(0x00);
   }

   protected void joinMessenger(MaplePacketLittleEndianWriter writer, MessengerJoin packet) {
      writer.write(0x01);
      writer.write(packet.position());
   }

   protected void messengerChat(MaplePacketLittleEndianWriter writer, MessengerChat packet) {
      writer.write(0x06);
      writer.writeMapleAsciiString(packet.text());
   }

   protected void messengerNote(MaplePacketLittleEndianWriter writer, MessengerNote packet) {
      writer.write(packet.mode());
      writer.writeMapleAsciiString(packet.text());
      writer.write(packet.mode2());
   }
}
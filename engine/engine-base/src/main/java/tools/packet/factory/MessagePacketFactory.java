package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.message.BuddyFindReply;
import tools.packet.message.ChatText;
import tools.packet.message.ClearAvatarMegaphone;
import tools.packet.message.FindReply;
import tools.packet.message.GachaponMessage;
import tools.packet.message.GetAvatarMegaphone;
import tools.packet.message.GiveFameErrorResponse;
import tools.packet.message.GiveFameResponse;
import tools.packet.message.ItemMegaphone;
import tools.packet.message.MultiChat;
import tools.packet.message.MultiMegaphone;
import tools.packet.message.NotifyJobAdvance;
import tools.packet.message.NotifyLevelUp;
import tools.packet.message.NotifyMarriage;
import tools.packet.message.ReceiveFame;
import tools.packet.message.ServerMessage;
import tools.packet.message.ServerNotice;
import tools.packet.message.SpouseMessage;
import tools.packet.message.Whisper;
import tools.packet.message.WhisperReply;
import tools.packet.message.YellowTip;

public class MessagePacketFactory extends AbstractPacketFactory {
   private static MessagePacketFactory instance;

   public static MessagePacketFactory getInstance() {
      if (instance == null) {
         instance = new MessagePacketFactory();
      }
      return instance;
   }

   private MessagePacketFactory() {
      Handler.handle(ServerNotice.class).decorate(this::serverNotice).register(registry);
      Handler.handle(ServerMessage.class).decorate(this::serverMessage).register(registry);
      Handler.handle(GetAvatarMegaphone.class).decorate(this::getAvatarMega).register(registry);
      Handler.handle(ClearAvatarMegaphone.class).decorate(this::byeAvatarMega).register(registry);
      Handler.handle(GachaponMessage.class).decorate(this::gachaponMessage).register(registry);
      Handler.handle(ChatText.class).decorate(this::getChatText).register(registry);
      Handler.handle(Whisper.class).decorate(this::getWhisper).register(registry);
      Handler.handle(WhisperReply.class).decorate(this::getWhisperReply).register(registry);
      Handler.handle(GiveFameResponse.class).decorate(this::giveFameResponse).register(registry);
      Handler.handle(GiveFameErrorResponse.class).decorate(this::giveFameErrorResponse).register(registry);
      Handler.handle(ReceiveFame.class).decorate(this::receiveFame).register(registry);
      Handler.handle(MultiChat.class).decorate(this::multiChat).register(registry);
      Handler.handle(FindReply.class).decorate(this::getFindReply).register(registry);
      Handler.handle(BuddyFindReply.class).decorate(this::getBuddyFindReply).register(registry);
      Handler.handle(ItemMegaphone.class).decorate(this::itemMegaphone).register(registry);
      Handler.handle(MultiMegaphone.class).decorate(this::getMultiMegaphone).register(registry);
      Handler.handle(NotifyLevelUp.class).decorate(this::levelUpMessage).register(registry);
      Handler.handle(NotifyMarriage.class).decorate(this::marriageMessage).register(registry);
      Handler.handle(NotifyJobAdvance.class).decorate(this::jobMessage).register(registry);
      Handler.handle(SpouseMessage.class).decorate(this::coupleMessage).register(registry);
      Handler.handle(YellowTip.class).decorate(this::sendYellowTip).register(registry);
   }

   /**
    * Gets a server notice packet.
    * <p>
    * Possible values for <code>type</code>:<br> 0: [Notice]<br> 1: Popup<br>
    * 2: Megaphone<br> 3: Super Megaphone<br> 4: Scrolling message at top<br>
    * 5: Pink Text<br> 6: Lightblue Text
    */
   protected void serverNotice(MaplePacketLittleEndianWriter writer, ServerNotice packet) {
      serverMessageInternal(writer, packet.theType(), packet.channel(), packet.message(), false, packet.smegaEar(), 0);
   }

   /**
    * Gets a server message packet.
    */
   protected void serverMessage(MaplePacketLittleEndianWriter writer, ServerMessage packet) {
      serverMessageInternal(writer, 4, (byte) 0, packet.message(), true, false, 0);
   }

   /**
    * Gets a server message packet.
    * <p>
    * Possible values for <code>type</code>:<br> 0: [Notice]<br> 1: Popup<br>
    * 2: Megaphone<br> 3: Super Megaphone<br> 4: Scrolling message at top<br>
    * 5: Pink Text<br> 6: Lightblue Text<br> 7: BroadCasting NPC
    *
    * @param type          The type of the notice.
    * @param channel       The channel this notice was sent on.
    * @param message       The message to convey.
    * @param serverMessage Is this a scrolling ticker?
    */
   protected void serverMessageInternal(MaplePacketLittleEndianWriter writer, int type, int channel, String message, boolean serverMessage, boolean megaEar, int npc) {
      writer.write(type);
      if (serverMessage) {
         writer.write(1);
      }
      writer.writeMapleAsciiString(message);
      if (type == 3) {
         writer.write(channel - 1); // channel
         writer.writeBool(megaEar);
      } else if (type == 6) {
         writer.writeInt(0);
      } else if (type == 7) { // npc
         writer.writeInt(npc);
      }
   }

   /**
    * Sends a Avatar Super Megaphone packet.
    */
   protected void getAvatarMega(MaplePacketLittleEndianWriter writer, GetAvatarMegaphone packet) {
      writer.writeInt(packet.getItemId());
      writer.writeMapleAsciiString(packet.getMedal() + packet.getCharacter().getName());
      for (String s : packet.getMessages()) {
         writer.writeMapleAsciiString(s);
      }
      writer.writeInt(packet.getChannel() - 1); // channel
      writer.writeBool(packet.isEar());
      addCharLook(writer, packet.getCharacter(), true);
   }

   /*
    * Sends a packet to remove the tiger megaphone
    * @return
    */
   protected void byeAvatarMega(MaplePacketLittleEndianWriter writer, ClearAvatarMegaphone packet) {
      writer.write(1);
   }

   /**
    * Sends the Gachapon green message when a user uses a gachapon ticket.
    */
   protected void gachaponMessage(MaplePacketLittleEndianWriter writer, GachaponMessage packet) {
      writer.write(0x0B);
      writer.writeMapleAsciiString(packet.characterName() + " : got a(n)");
      writer.writeInt(0); //random?
      writer.writeMapleAsciiString(packet.town());
      addItemInfo(writer, packet.item(), true);
   }

   /**
    * Gets a general chat packet.
    */
   protected void getChatText(MaplePacketLittleEndianWriter writer, ChatText packet) {
      writer.writeInt(packet.characterIdFrom());
      writer.writeBool(packet.gm());
      writer.writeMapleAsciiString(packet.text());
      writer.write(packet.show());
   }

   protected void getWhisper(MaplePacketLittleEndianWriter writer, Whisper packet) {
      writer.write(0x12);
      writer.writeMapleAsciiString(packet.sender());
      writer.writeShort(packet.channel() - 1); // I guess this is the channel
      writer.writeMapleAsciiString(packet.text());
   }

   protected void getWhisperReply(MaplePacketLittleEndianWriter writer, WhisperReply packet) {
      writer.write(0x0A); // whisper?
      writer.writeMapleAsciiString(packet.target());
      writer.write(packet.reply());
   }

   protected void giveFameResponse(MaplePacketLittleEndianWriter writer, GiveFameResponse packet) {
      writer.write(0);
      writer.writeMapleAsciiString(packet.characterName());
      writer.write(packet.mode());
      writer.writeShort(packet.newFame());
      writer.writeShort(0);
   }

   /**
    * status can be: <br> 0: ok, use giveFameResponse<br> 1: the username is
    * incorrectly entered<br> 2: users under level 15 are unable to toggle with
    * fame.<br> 3: can't raise or drop fame anymore today.<br> 4: can't raise
    * or drop fame for this character for this month anymore.<br> 5: received
    * fame, use receiveFame()<br> 6: level of fame neither has been raised nor
    * dropped due to an unexpected error
    */
   protected void giveFameErrorResponse(MaplePacketLittleEndianWriter writer, GiveFameErrorResponse packet) {
      writer.write(packet.status());
   }

   protected void receiveFame(MaplePacketLittleEndianWriter writer, ReceiveFame packet) {
      writer.write(5);
      writer.writeMapleAsciiString(packet.characterNameFrom());
      writer.write(packet.mode());
   }

   /**
    * mode: 0 buddy chat; 1 party chat; 2 guild chat
    */
   protected void multiChat(MaplePacketLittleEndianWriter writer, MultiChat packet) {
      writer.write(packet.mode());
      writer.writeMapleAsciiString(packet.name());
      writer.writeMapleAsciiString(packet.text());
   }

   protected void getFindReply(MaplePacketLittleEndianWriter writer, FindReply packet) {
      writer.write(9);
      writer.writeMapleAsciiString(packet.target());
      writer.write(packet.mapType()); // 0: mts 1: map 2: cs
      writer.writeInt(packet.mapId()); // -1 if mts, cs
      if (packet.mapType() == 1) {
         writer.write(new byte[8]);
      }
   }

   protected void getBuddyFindReply(MaplePacketLittleEndianWriter writer, BuddyFindReply packet) {
      writer.write(72);
      writer.writeMapleAsciiString(packet.target());
      writer.write(packet.mapType()); // 0: mts 1: map 2: cs
      writer.writeInt(packet.mapId()); // -1 if mts, cs
      if (packet.mapType() == 1) {
         writer.write(new byte[8]);
      }
   }

   protected void itemMegaphone(MaplePacketLittleEndianWriter writer, ItemMegaphone packet) {
      writer.write(8);
      writer.writeMapleAsciiString(packet.message());
      writer.write(packet.channel() - 1);
      writer.write(packet.whisper() ? 1 : 0);
      if (packet.item() == null) {
         writer.write(0);
      } else {
         writer.write(packet.item().position());
         addItemInfo(writer, packet.item(), true);
      }
   }

   protected void getMultiMegaphone(MaplePacketLittleEndianWriter writer, MultiMegaphone packet) {
      writer.write(0x0A);
      if (packet.messages()[0] != null) {
         writer.writeMapleAsciiString(packet.messages()[0]);
      }
      writer.write(packet.messages().length);
      for (int i = 1; i < packet.messages().length; i++) {
         if (packet.messages()[i] != null) {
            writer.writeMapleAsciiString(packet.messages()[i]);
         }
      }
      for (int i = 0; i < 10; i++) {
         writer.write(packet.channel() - 1);
      }
      writer.write(packet.showEar() ? 1 : 0);
      writer.write(1);
   }

   /**
    * Sends a "level up" packet to the guild or family.
    * <p>
    * Possible values for <code>type</code>:<br> 0: <Family> ? has reached Lv.
    * ?.<br> - The Reps you have received from ? will be reduced in half. 1:
    * <Family> ? has reached Lv. ?.<br> 2: <Guild> ? has reached Lv. ?.<br>
    */
   protected void levelUpMessage(MaplePacketLittleEndianWriter writer, NotifyLevelUp packet) {
      writer.write(packet.theType());
      writer.writeInt(packet.level());
      writer.writeMapleAsciiString(packet.characterName());
   }

   /**
    * Sends a "married" packet to the guild or family.
    * <p>
    * Possible values for <code>type</code>:<br> 0: <Guild ? is now married.
    * Please congratulate them.<br> 1: <Family ? is now married. Please
    * congratulate them.<br>
    */
   protected void marriageMessage(MaplePacketLittleEndianWriter writer, NotifyMarriage packet) {
      writer.write(packet.theType());  // 0: guild, 1: family
      writer.writeMapleAsciiString("> " + packet.characterName()); //To fix the stupid packet lol
   }

   /**
    * Sends a "job advance" packet to the guild or family.
    * <p>
    * Possible values for <code>type</code>:<br> 0: <Guild ? has advanced to
    * a(an) ?.<br> 1: <Family ? has advanced to a(an) ?.<br>
    */
   protected void jobMessage(MaplePacketLittleEndianWriter writer, NotifyJobAdvance packet) {
      writer.write(packet.theType());
      writer.writeInt(packet.job());
      writer.writeMapleAsciiString("> " + packet.characterName()); //To fix the stupid packet lol
   }

   protected void coupleMessage(MaplePacketLittleEndianWriter writer, SpouseMessage packet) {
      writer.write(packet.spouse() ? 5 : 4); // v2 = CInPacket::Decode1(a1) - 4;
      if (packet.spouse()) { // if ( v2 ) {
         writer.writeMapleAsciiString(packet.fiance());
      }
      writer.write(packet.spouse() ? 5 : 1);
      writer.writeMapleAsciiString(packet.text());
   }

   protected void sendYellowTip(MaplePacketLittleEndianWriter writer, YellowTip packet) {
      writer.write(0xFF);
      writer.writeMapleAsciiString(packet.tip());
      writer.writeShort(0);
   }
}
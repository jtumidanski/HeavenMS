package tools.packet.factory;

import net.opcodes.SendOpcode;
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
      registry.setHandler(ServerNotice.class, packet -> create(SendOpcode.SERVERMESSAGE, this::serverNotice, packet));
      registry.setHandler(ServerMessage.class, packet -> create(SendOpcode.SERVERMESSAGE, this::serverMessage, packet));
      registry.setHandler(GetAvatarMegaphone.class, packet -> create(SendOpcode.SET_AVATAR_MEGAPHONE, this::getAvatarMega, packet));
      registry.setHandler(ClearAvatarMegaphone.class, packet -> create(SendOpcode.CLEAR_AVATAR_MEGAPHONE, this::byeAvatarMega, packet));
      registry.setHandler(GachaponMessage.class, packet -> create(SendOpcode.SERVERMESSAGE, this::gachaponMessage, packet));
      registry.setHandler(ChatText.class, packet -> create(SendOpcode.CHATTEXT, this::getChatText, packet));
      registry.setHandler(Whisper.class, packet -> create(SendOpcode.WHISPER, this::getWhisper, packet));
      registry.setHandler(WhisperReply.class, packet -> create(SendOpcode.WHISPER, this::getWhisperReply, packet));
      registry.setHandler(GiveFameResponse.class, packet -> create(SendOpcode.FAME_RESPONSE, this::giveFameResponse, packet));
      registry.setHandler(GiveFameErrorResponse.class, packet -> create(SendOpcode.FAME_RESPONSE, this::giveFameErrorResponse, packet));
      registry.setHandler(ReceiveFame.class, packet -> create(SendOpcode.FAME_RESPONSE, this::receiveFame, packet));
      registry.setHandler(MultiChat.class, packet -> create(SendOpcode.MULTICHAT, this::multiChat, packet));
      registry.setHandler(FindReply.class, packet -> create(SendOpcode.WHISPER, this::getFindReply, packet));
      registry.setHandler(BuddyFindReply.class, packet -> create(SendOpcode.WHISPER, this::getBuddyFindReply, packet));
      registry.setHandler(ItemMegaphone.class, packet -> create(SendOpcode.SERVERMESSAGE, this::itemMegaphone, packet));
      registry.setHandler(MultiMegaphone.class, packet -> create(SendOpcode.SERVERMESSAGE, this::getMultiMegaphone, packet));
      registry.setHandler(NotifyLevelUp.class, packet -> create(SendOpcode.NOTIFY_LEVELUP, this::levelUpMessage, packet));
      registry.setHandler(NotifyMarriage.class, packet -> create(SendOpcode.NOTIFY_MARRIAGE, this::marriageMessage, packet));
      registry.setHandler(NotifyJobAdvance.class, packet -> create(SendOpcode.NOTIFY_JOB_CHANGE, this::jobMessage, packet));
      registry.setHandler(SpouseMessage.class, packet -> create(SendOpcode.SPOUSE_CHAT, this::coupleMessage, packet));
      registry.setHandler(YellowTip.class, packet -> create(SendOpcode.SET_WEEK_EVENT_MESSAGE, this::sendYellowTip, packet));
   }

   /**
    * Gets a server notice packet.
    * <p>
    * Possible values for <code>type</code>:<br> 0: [Notice]<br> 1: Popup<br>
    * 2: Megaphone<br> 3: Super Megaphone<br> 4: Scrolling message at top<br>
    * 5: Pink Text<br> 6: Lightblue Text
    *
    * @return The server notice packet.
    */
   protected void serverNotice(MaplePacketLittleEndianWriter writer, ServerNotice packet) {
      serverMessageInternal(writer, packet.theType(), packet.channel(), packet.message(), false, packet.smegaEar(), 0);
   }

   /**
    * Gets a server message packet.
    *
    * @return The server message packet.
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
    * @param servermessage Is this a scrolling ticker?
    * @return The server notice packet.
    */
   protected void serverMessageInternal(MaplePacketLittleEndianWriter writer, int type, int channel, String message, boolean servermessage, boolean megaEar, int npc) {
      writer.write(type);
      if (servermessage) {
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
    *
    * @return
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
    *
    * @return
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
    *
    * @return The general chat packet.
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

   /**
    * @return the MaplePacket
    */
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
    *
    * @return
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
    * mode: 0 buddychat; 1 partychat; 2 guildchat
    *
    * @return
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
    * Sends a "levelup" packet to the guild or family.
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
      writer.writeInt(packet.job()); //Why fking int?
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
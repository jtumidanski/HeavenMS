package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ServerNotice) {
         return create(this::serverNotice, packetInput);
      } else if (packetInput instanceof ServerMessage) {
         return create(this::serverMessage, packetInput);
      } else if (packetInput instanceof GetAvatarMegaphone) {
         return create(this::getAvatarMega, packetInput);
      } else if (packetInput instanceof ClearAvatarMegaphone) {
         return create(this::byeAvatarMega, packetInput);
      } else if (packetInput instanceof GachaponMessage) {
         return create(this::gachaponMessage, packetInput);
      } else if (packetInput instanceof ChatText) {
         return create(this::getChatText, packetInput);
      } else if (packetInput instanceof Whisper) {
         return create(this::getWhisper, packetInput);
      } else if (packetInput instanceof WhisperReply) {
         return create(this::getWhisperReply, packetInput);
      } else if (packetInput instanceof GiveFameResponse) {
         return create(this::giveFameResponse, packetInput);
      } else if (packetInput instanceof GiveFameErrorResponse) {
         return create(this::giveFameErrorResponse, packetInput);
      } else if (packetInput instanceof ReceiveFame) {
         return create(this::receiveFame, packetInput);
      } else if (packetInput instanceof MultiChat) {
         return create(this::multiChat, packetInput);
      } else if (packetInput instanceof FindReply) {
         return create(this::getFindReply, packetInput);
      } else if (packetInput instanceof BuddyFindReply) {
         return create(this::getBuddyFindReply, packetInput);
      } else if (packetInput instanceof ItemMegaphone) {
         return create(this::itemMegaphone, packetInput);
      } else if (packetInput instanceof MultiMegaphone) {
         return create(this::getMultiMegaphone, packetInput);
      } else if (packetInput instanceof NotifyLevelUp) {
         return create(this::levelUpMessage, packetInput);
      } else if (packetInput instanceof NotifyMarriage) {
         return create(this::marriageMessage, packetInput);
      } else if (packetInput instanceof NotifyJobAdvance) {
         return create(this::jobMessage, packetInput);
      } else if (packetInput instanceof SpouseMessage) {
         return create(this::coupleMessage, packetInput);
      } else if (packetInput instanceof YellowTip) {
         return create(this::sendYellowTip, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
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
   protected byte[] serverNotice(ServerNotice packet) {
      return serverMessageInternal(packet.theType(), packet.channel(), packet.message(), false, packet.smegaEar(), 0);
   }

   /**
    * Gets a server message packet.
    *
    * @return The server message packet.
    */
   protected byte[] serverMessage(ServerMessage packet) {
      return serverMessageInternal(4, (byte) 0, packet.message(), true, false, 0);
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
   protected byte[] serverMessageInternal(int type, int channel, String message, boolean servermessage, boolean megaEar, int npc) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SERVERMESSAGE.getValue());
      mplew.write(type);
      if (servermessage) {
         mplew.write(1);
      }
      mplew.writeMapleAsciiString(message);
      if (type == 3) {
         mplew.write(channel - 1); // channel
         mplew.writeBool(megaEar);
      } else if (type == 6) {
         mplew.writeInt(0);
      } else if (type == 7) { // npc
         mplew.writeInt(npc);
      }
      return mplew.getPacket();
   }

   /**
    * Sends a Avatar Super Megaphone packet.
    *
    * @return
    */
   protected byte[] getAvatarMega(GetAvatarMegaphone packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_AVATAR_MEGAPHONE.getValue());
      mplew.writeInt(packet.getItemId());
      mplew.writeMapleAsciiString(packet.getMedal() + packet.getCharacter().getName());
      for (String s : packet.getMessages()) {
         mplew.writeMapleAsciiString(s);
      }
      mplew.writeInt(packet.getChannel() - 1); // channel
      mplew.writeBool(packet.isEar());
      addCharLook(mplew, packet.getCharacter(), true);
      return mplew.getPacket();
   }

   /*
    * Sends a packet to remove the tiger megaphone
    * @return
    */
   protected byte[] byeAvatarMega(ClearAvatarMegaphone packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CLEAR_AVATAR_MEGAPHONE.getValue());
      mplew.write(1);
      return mplew.getPacket();
   }

   /**
    * Sends the Gachapon green message when a user uses a gachapon ticket.
    *
    * @return
    */
   protected byte[] gachaponMessage(GachaponMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SERVERMESSAGE.getValue());
      mplew.write(0x0B);
      mplew.writeMapleAsciiString(packet.characterName() + " : got a(n)");
      mplew.writeInt(0); //random?
      mplew.writeMapleAsciiString(packet.town());
      addItemInfo(mplew, packet.item(), true);
      return mplew.getPacket();
   }

   /**
    * Gets a general chat packet.
    *
    * @return The general chat packet.
    */
   protected byte[] getChatText(ChatText packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CHATTEXT.getValue());
      mplew.writeInt(packet.characterIdFrom());
      mplew.writeBool(packet.gm());
      mplew.writeMapleAsciiString(packet.text());
      mplew.write(packet.show());
      return mplew.getPacket();
   }

   protected byte[] getWhisper(Whisper packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.WHISPER.getValue());
      mplew.write(0x12);
      mplew.writeMapleAsciiString(packet.sender());
      mplew.writeShort(packet.channel() - 1); // I guess this is the channel
      mplew.writeMapleAsciiString(packet.text());
      return mplew.getPacket();
   }

   /**
    * @return the MaplePacket
    */
   protected byte[] getWhisperReply(WhisperReply packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.WHISPER.getValue());
      mplew.write(0x0A); // whisper?
      mplew.writeMapleAsciiString(packet.target());
      mplew.write(packet.reply());
      return mplew.getPacket();
   }

   protected byte[] giveFameResponse(GiveFameResponse packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAME_RESPONSE.getValue());
      mplew.write(0);
      mplew.writeMapleAsciiString(packet.characterName());
      mplew.write(packet.mode());
      mplew.writeShort(packet.newFame());
      mplew.writeShort(0);
      return mplew.getPacket();
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
   protected byte[] giveFameErrorResponse(GiveFameErrorResponse packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAME_RESPONSE.getValue());
      mplew.write(packet.status());
      return mplew.getPacket();
   }

   protected byte[] receiveFame(ReceiveFame packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FAME_RESPONSE.getValue());
      mplew.write(5);
      mplew.writeMapleAsciiString(packet.characterNameFrom());
      mplew.write(packet.mode());
      return mplew.getPacket();
   }

   /**
    * mode: 0 buddychat; 1 partychat; 2 guildchat
    *
    * @return
    */
   protected byte[] multiChat(MultiChat packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MULTICHAT.getValue());
      mplew.write(packet.mode());
      mplew.writeMapleAsciiString(packet.name());
      mplew.writeMapleAsciiString(packet.text());
      return mplew.getPacket();
   }

   protected byte[] getFindReply(FindReply packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.WHISPER.getValue());
      mplew.write(9);
      mplew.writeMapleAsciiString(packet.target());
      mplew.write(packet.mapType()); // 0: mts 1: map 2: cs
      mplew.writeInt(packet.mapId()); // -1 if mts, cs
      if (packet.mapType() == 1) {
         mplew.write(new byte[8]);
      }
      return mplew.getPacket();
   }

   protected byte[] getBuddyFindReply(BuddyFindReply packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.WHISPER.getValue());
      mplew.write(72);
      mplew.writeMapleAsciiString(packet.target());
      mplew.write(packet.mapType()); // 0: mts 1: map 2: cs
      mplew.writeInt(packet.mapId()); // -1 if mts, cs
      if (packet.mapType() == 1) {
         mplew.write(new byte[8]);
      }
      return mplew.getPacket();
   }

   protected byte[] itemMegaphone(ItemMegaphone packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SERVERMESSAGE.getValue());
      mplew.write(8);
      mplew.writeMapleAsciiString(packet.message());
      mplew.write(packet.channel() - 1);
      mplew.write(packet.whisper() ? 1 : 0);
      if (packet.item() == null) {
         mplew.write(0);
      } else {
         mplew.write(packet.item().position());
         addItemInfo(mplew, packet.item(), true);
      }
      return mplew.getPacket();
   }

   protected byte[] getMultiMegaphone(MultiMegaphone packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SERVERMESSAGE.getValue());
      mplew.write(0x0A);
      if (packet.messages()[0] != null) {
         mplew.writeMapleAsciiString(packet.messages()[0]);
      }
      mplew.write(packet.messages().length);
      for (int i = 1; i < packet.messages().length; i++) {
         if (packet.messages()[i] != null) {
            mplew.writeMapleAsciiString(packet.messages()[i]);
         }
      }
      for (int i = 0; i < 10; i++) {
         mplew.write(packet.channel() - 1);
      }
      mplew.write(packet.showEar() ? 1 : 0);
      mplew.write(1);
      return mplew.getPacket();
   }

   /**
    * Sends a "levelup" packet to the guild or family.
    * <p>
    * Possible values for <code>type</code>:<br> 0: <Family> ? has reached Lv.
    * ?.<br> - The Reps you have received from ? will be reduced in half. 1:
    * <Family> ? has reached Lv. ?.<br> 2: <Guild> ? has reached Lv. ?.<br>
    */
   protected byte[] levelUpMessage(NotifyLevelUp packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NOTIFY_LEVELUP.getValue());
      mplew.write(packet.theType());
      mplew.writeInt(packet.level());
      mplew.writeMapleAsciiString(packet.characterName());

      return mplew.getPacket();
   }

   /**
    * Sends a "married" packet to the guild or family.
    * <p>
    * Possible values for <code>type</code>:<br> 0: <Guild ? is now married.
    * Please congratulate them.<br> 1: <Family ? is now married. Please
    * congratulate them.<br>
    */
   protected byte[] marriageMessage(NotifyMarriage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NOTIFY_MARRIAGE.getValue());
      mplew.write(packet.theType());  // 0: guild, 1: family
      mplew.writeMapleAsciiString("> " + packet.characterName()); //To fix the stupid packet lol

      return mplew.getPacket();
   }

   /**
    * Sends a "job advance" packet to the guild or family.
    * <p>
    * Possible values for <code>type</code>:<br> 0: <Guild ? has advanced to
    * a(an) ?.<br> 1: <Family ? has advanced to a(an) ?.<br>
    */
   protected byte[] jobMessage(NotifyJobAdvance packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NOTIFY_JOB_CHANGE.getValue());
      mplew.write(packet.theType());
      mplew.writeInt(packet.job()); //Why fking int?
      mplew.writeMapleAsciiString("> " + packet.characterName()); //To fix the stupid packet lol

      return mplew.getPacket();
   }

   protected byte[] coupleMessage(SpouseMessage packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SPOUSE_CHAT.getValue());
      mplew.write(packet.spouse() ? 5 : 4); // v2 = CInPacket::Decode1(a1) - 4;
      if (packet.spouse()) { // if ( v2 ) {
         mplew.writeMapleAsciiString(packet.fiance());
      }
      mplew.write(packet.spouse() ? 5 : 1);
      mplew.writeMapleAsciiString(packet.text());
      return mplew.getPacket();
   }

   protected byte[] sendYellowTip(YellowTip packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_WEEK_EVENT_MESSAGE.getValue());
      mplew.write(0xFF);
      mplew.writeMapleAsciiString(packet.tip());
      mplew.writeShort(0);
      return mplew.getPacket();
   }
}
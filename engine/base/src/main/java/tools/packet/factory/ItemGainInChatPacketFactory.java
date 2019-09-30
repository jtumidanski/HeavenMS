package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.showitemgaininchat.ShowBuybackEffect;
import tools.packet.showitemgaininchat.ShowGainCard;
import tools.packet.showitemgaininchat.ShowInfo;
import tools.packet.showitemgaininchat.ShowIntro;
import tools.packet.showitemgaininchat.ShowItemGainInChat;
import tools.packet.showitemgaininchat.ShowMakerEffect;
import tools.packet.showitemgaininchat.ShowOwnBerserk;
import tools.packet.showitemgaininchat.ShowOwnBuffEffect;
import tools.packet.showitemgaininchat.ShowOwnPetLevelUp;
import tools.packet.showitemgaininchat.ShowOwnRecovery;
import tools.packet.showitemgaininchat.ShowSpecialEffect;
import tools.packet.showitemgaininchat.ShowWheelsLeft;

public class ItemGainInChatPacketFactory extends AbstractPacketFactory {
   private static ItemGainInChatPacketFactory instance;

   public static ItemGainInChatPacketFactory getInstance() {
      if (instance == null) {
         instance = new ItemGainInChatPacketFactory();
      }
      return instance;
   }

   private ItemGainInChatPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ShowItemGainInChat) {
         return create(this::getShowItemGain, packetInput);
      } else if (packetInput instanceof ShowOwnBuffEffect) {
         return create(this::showOwnBuffEffect, packetInput);
      } else if (packetInput instanceof ShowOwnBerserk) {
         return create(this::showOwnBerserk, packetInput);
      } else if (packetInput instanceof ShowOwnPetLevelUp) {
         return create(this::showOwnPetLevelUp, packetInput);
      } else if (packetInput instanceof ShowGainCard) {
         return create(this::showGainCard, packetInput);
      } else if (packetInput instanceof ShowIntro) {
         return create(this::showIntro, packetInput);
      } else if (packetInput instanceof ShowInfo) {
         return create(this::showInfo, packetInput);
      } else if (packetInput instanceof ShowBuybackEffect) {
         return create(this::showBuybackEffect, packetInput);
      } else if (packetInput instanceof ShowSpecialEffect) {
         return create(this::showSpecialEffect, packetInput);
      } else if (packetInput instanceof ShowMakerEffect) {
         return create(this::showMakerEffect, packetInput);
      } else if (packetInput instanceof ShowOwnRecovery) {
         return create(this::showOwnRecovery, packetInput);
      } else if (packetInput instanceof ShowWheelsLeft) {
         return create(this::showWheelsLeft, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Gets a packet telling the client to show an item gain.
    *
    * @return The item gain packet.
    */
   protected byte[] getShowItemGain(ShowItemGainInChat packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(3);
      mplew.write(1);
      mplew.writeInt(packet.itemId());
      mplew.writeInt(packet.quantity());
      return mplew.getPacket();
   }

   protected byte[] showOwnBuffEffect(ShowOwnBuffEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(packet.effectId());
      mplew.writeInt(packet.skillId());
      mplew.write(0xA9);
      mplew.write(1);
      return mplew.getPacket();
   }

   protected byte[] showOwnBerserk(ShowOwnBerserk packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(1);
      mplew.writeInt(1320006);
      mplew.write(0xA9);
      mplew.write(packet.skillLevel());
      mplew.write(packet.berserk() ? 1 : 0);
      return mplew.getPacket();
   }

   protected byte[] showOwnPetLevelUp(ShowOwnPetLevelUp packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(4);
      mplew.write(0);
      mplew.write(packet.index()); // Pet Index
      return mplew.getPacket();
   }

   protected byte[] showGainCard(ShowGainCard packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(0x0D);
      return mplew.getPacket();
   }

   protected byte[] showIntro(ShowIntro packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(0x12);
      mplew.writeMapleAsciiString(packet.path());
      return mplew.getPacket();
   }

   protected byte[] showInfo(ShowInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(0x17);
      mplew.writeMapleAsciiString(packet.path());
      mplew.writeInt(1);
      return mplew.getPacket();
   }

   protected byte[] showBuybackEffect(ShowBuybackEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(11);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   /**
    * 0 = Levelup 6 = Exp did not drop (Safety Charms) 7 = Enter portal sound
    * 8 = Job change 9 = Quest complete 10 = Recovery 11 = Buff effect
    * 14 = Monster book pickup 15 = Equipment levelup 16 = Maker Skill Success
    * 17 = Buff effect w/ sfx 19 = Exp card [500, 200, 50] 21 = Wheel of destiny
    * 26 = Spirit Stone
    *
    * @return
    */
   protected byte[] showSpecialEffect(ShowSpecialEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(packet.effectId());
      return mplew.getPacket();
   }

   protected byte[] showMakerEffect(ShowMakerEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(16);
      mplew.writeInt(packet.success() ? 0 : 1);
      return mplew.getPacket();
   }

   protected byte[] showOwnRecovery(ShowOwnRecovery packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(0x0A);
      mplew.write(packet.amount());
      return mplew.getPacket();
   }

   protected byte[] showWheelsLeft(ShowWheelsLeft packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
      mplew.write(0x15);
      mplew.write(packet.remaining());
      return mplew.getPacket();
   }
}
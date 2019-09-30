package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.statusinfo.CompleteQuest;
import tools.packet.statusinfo.GetDojoInfo;
import tools.packet.statusinfo.GetDojoInfoMessage;
import tools.packet.statusinfo.GetGuildPointMessage;
import tools.packet.statusinfo.GetItemMessage;
import tools.packet.statusinfo.ShowBunny;
import tools.packet.statusinfo.ShowEXPGain;
import tools.packet.statusinfo.ShowFameGain;
import tools.packet.statusinfo.ShowInfoText;
import tools.packet.statusinfo.ShowInventoryFull;
import tools.packet.statusinfo.ShowItemExpired;
import tools.packet.statusinfo.ShowItemGain;
import tools.packet.statusinfo.ShowItemUnavailable;
import tools.packet.statusinfo.ShowMesoGain;
import tools.packet.statusinfo.ShowQuestForfeit;
import tools.packet.statusinfo.UpdateAreaInfo;
import tools.packet.statusinfo.UpdateDojoStats;
import tools.packet.statusinfo.UpdateQuest;

public class StatusInfoPacketFactory extends AbstractPacketFactory {
   private static StatusInfoPacketFactory instance;

   public static StatusInfoPacketFactory getInstance() {
      if (instance == null) {
         instance = new StatusInfoPacketFactory();
      }
      return instance;
   }

   private StatusInfoPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ShowEXPGain) {
         return create(this::getShowExpGain, packetInput);
      } else if (packetInput instanceof ShowFameGain) {
         return create(this::getShowFameGain, packetInput);
      } else if (packetInput instanceof ShowMesoGain) {
         return create(this::getShowMesoGain, packetInput);
      } else if (packetInput instanceof ShowQuestForfeit) {
         return create(this::forfeitQuest, packetInput);
      } else if (packetInput instanceof CompleteQuest) {
         return create(this::completeQuest, packetInput);
      } else if (packetInput instanceof UpdateQuest) {
         return create(this::updateQuest, packetInput);
      } else if (packetInput instanceof ShowInventoryFull) {
         return create(this::getShowInventoryFull, packetInput);
      } else if (packetInput instanceof ShowItemUnavailable) {
         return create(this::showItemUnavailable, packetInput);
      } else if (packetInput instanceof UpdateAreaInfo) {
         return create(this::updateAreaInfo, packetInput);
      } else if (packetInput instanceof GetGuildPointMessage) {
         return create(this::getGPMessage, packetInput);
      } else if (packetInput instanceof GetItemMessage) {
         return create(this::getItemMessage, packetInput);
      } else if (packetInput instanceof ShowInfoText) {
         return create(this::showInfoText, packetInput);
      } else if (packetInput instanceof GetDojoInfo) {
         return create(this::getDojoInfo, packetInput);
      } else if (packetInput instanceof GetDojoInfoMessage) {
         return create(this::getDojoInfoMessage, packetInput);
      } else if (packetInput instanceof UpdateDojoStats) {
         return create(this::updateDojoStats, packetInput);
      } else if (packetInput instanceof ShowItemExpired) {
         return create(this::itemExpired, packetInput);
      } else if (packetInput instanceof ShowBunny) {
         return create(this::bunnyPacket, packetInput);
      } else if (packetInput instanceof ShowItemGain) {
         return create(this::getShowItemGain, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Gets a packet telling the client to show an EXP increase.
    *
    * @return The exp gained packet.
    */
   protected byte[] getShowExpGain(ShowEXPGain packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(3); // 3 = exp, 4 = fame, 5 = mesos, 6 = guildpoints
      mplew.writeBool(packet.white());
      mplew.writeInt(packet.gain());
      mplew.writeBool(packet.inChat());
      mplew.writeInt(0); // bonus event exp
      mplew.write(0); // third monster kill event
      mplew.write(0); // RIP byte, this is always a 0
      mplew.writeInt(0); //wedding bonus
      if (packet.inChat()) { // quest bonus rate stuff
         mplew.write(0);
      }

      mplew.write(0); //0 = party bonus, 100 = 1x Bonus EXP, 200 = 2x Bonus EXP
      mplew.writeInt(packet.party()); // party bonus
      mplew.writeInt(packet.equip()); //equip bonus
      mplew.writeInt(0); //Internet Cafe Bonus
      mplew.writeInt(0); //Rainbow Week Bonus
      return mplew.getPacket();
   }

   /**
    * Gets a packet telling the client to show a fame gain.
    *
    * @return The meso gain packet.
    */
   protected byte[] getShowFameGain(ShowFameGain packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(4);
      mplew.writeInt(packet.gain());
      return mplew.getPacket();
   }

   /**
    * Gets a packet telling the client to show a meso gain.
    *
    * @return The meso gain packet.
    */
   protected byte[] getShowMesoGain(ShowMesoGain packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      if (!packet.inChat()) {
         mplew.write(0);
         mplew.writeShort(1); //v83
      } else {
         mplew.write(5);
      }
      mplew.writeInt(packet.gain());
      mplew.writeShort(0);
      return mplew.getPacket();
   }

   protected byte[] forfeitQuest(ShowQuestForfeit packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(1);
      mplew.writeShort(packet.questId());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] completeQuest(CompleteQuest packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(1);
      mplew.writeShort(packet.questId());
      mplew.write(2);
      mplew.writeLong(getTime(packet.time()));
      return mplew.getPacket();
   }

   protected byte[] updateQuest(UpdateQuest packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(1);
      mplew.writeShort(packet.infoUpdate() ? packet.infoNumber() : packet.questId());
      if (packet.infoUpdate()) {
         mplew.write(1);
      } else {
         mplew.write(packet.questStatusId());
      }

      mplew.writeMapleAsciiString(packet.questData());
      mplew.skip(5);
      return mplew.getPacket();
   }

   protected byte[] getShowInventoryFull(ShowInventoryFull packet) {
      return getShowInventoryStatus(0xff);
   }

   protected byte[] showItemUnavailable(ShowItemUnavailable packet) {
      return getShowInventoryStatus(0xfe);
   }

   protected byte[] getShowInventoryStatus(int mode) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(0);
      mplew.write(mode);
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] updateAreaInfo(UpdateAreaInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(0x0A); //0x0B in v95
      mplew.writeShort(packet.areaId());//infoNumber
      mplew.writeMapleAsciiString(packet.info());
      return mplew.getPacket();
   }

   protected byte[] getGPMessage(GetGuildPointMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(6);
      mplew.writeInt(packet.change());
      return mplew.getPacket();
   }

   protected byte[] getItemMessage(GetItemMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(7);
      mplew.writeInt(packet.itemId());
      return mplew.getPacket();
   }

   protected byte[] showInfoText(ShowInfoText packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(9);
      mplew.writeMapleAsciiString(packet.text());
      return mplew.getPacket();
   }

   protected byte[] getDojoInfo(GetDojoInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(10);
      mplew.write(new byte[]{(byte) 0xB7, 4});//QUEST ID f5
      mplew.writeMapleAsciiString(packet.info());
      return mplew.getPacket();
   }

   protected byte[] getDojoInfoMessage(GetDojoInfoMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(9);
      mplew.writeMapleAsciiString(packet.message());
      return mplew.getPacket();
   }

   protected byte[] updateDojoStats(UpdateDojoStats packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(10);
      mplew.write(new byte[]{(byte) 0xB7, 4}); //?
      mplew.writeMapleAsciiString("pt=" + packet.dojoPoints() + ";belt=" + packet.belt() + ";tuto=" + (packet.finishedDojoTutorial() ? "1" : "0"));
      return mplew.getPacket();
   }

   protected byte[] itemExpired(ShowItemExpired packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(2);
      mplew.writeInt(packet.itemId());
      return mplew.getPacket();
   }

   protected byte[] bunnyPacket(ShowBunny packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.write(9);
      mplew.writeAsciiString("Protect the Moon Bunny!!!");
      return mplew.getPacket();
   }

   /**
    * Gets a packet telling the client to show an item gain.
    *
    * @return The item gain packet.
    */
   protected byte[] getShowItemGain(ShowItemGain packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOW_STATUS_INFO.getValue());
      mplew.writeShort(0);
      mplew.writeInt(packet.itemId());
      mplew.writeInt(packet.quantity());
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }
}
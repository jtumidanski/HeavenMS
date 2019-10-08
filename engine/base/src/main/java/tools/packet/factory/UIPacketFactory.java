package tools.packet.factory;

import client.KeyBinding;
import net.opcodes.SendOpcode;
import net.server.SkillMacro;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.ui.DisableMiniMap;
import tools.packet.ui.DisableUI;
import tools.packet.ui.FinishedSort;
import tools.packet.ui.FinishedSort2;
import tools.packet.ui.GetClock;
import tools.packet.ui.GetClockTime;
import tools.packet.ui.GetKeyMap;
import tools.packet.ui.GetMacros;
import tools.packet.ui.LockUI;
import tools.packet.ui.OpenUI;
import tools.packet.ui.StopClock;

public class UIPacketFactory extends AbstractPacketFactory {
   private static UIPacketFactory instance;

   public static UIPacketFactory getInstance() {
      if (instance == null) {
         instance = new UIPacketFactory();
      }
      return instance;
   }

   private UIPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof OpenUI) {
         return create(this::openUI, packetInput);
      } else if (packetInput instanceof LockUI) {
         return create(this::lockUI, packetInput);
      } else if (packetInput instanceof DisableUI) {
         return create(this::disableUI, packetInput);
      } else if (packetInput instanceof GetKeyMap) {
         return create(this::getKeymap, packetInput);
      } else if (packetInput instanceof GetMacros) {
         return create(this::getMacros, packetInput);
      } else if (packetInput instanceof DisableMiniMap) {
         return create(this::disableMinimap, packetInput);
      } else if (packetInput instanceof FinishedSort) {
         return create(this::finishedSort, packetInput);
      } else if (packetInput instanceof FinishedSort2) {
         return create(this::finishedSort2, packetInput);
      } else if (packetInput instanceof GetClock) {
         return create(this::getClock, packetInput);
      } else if (packetInput instanceof GetClockTime) {
         return create(this::getClockTime, packetInput);
      } else if (packetInput instanceof StopClock) {
         return create(this::removeClock, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Sends a UI utility. 0x01 - Equipment Inventory. 0x02 - Stat Window. 0x03
    * - Skill Window. 0x05 - Keyboard Settings. 0x06 - Quest window. 0x09 -
    * Monsterbook Window. 0x0A - Char Info 0x0B - Guild BBS 0x12 - Monster
    * Carnival Window 0x16 - Party Search. 0x17 - Item Creation Window. 0x1A -
    * My Ranking O.O 0x1B - Family Window 0x1C - Family Pedigree 0x1D - GM
    * Story Board /funny shet 0x1E - Envelop saying you got mail from an admin.
    * lmfao 0x1F - Medal Window 0x20 - Maple Event (???) 0x21 - Invalid Pointer
    * Crash
    */
   protected byte[] openUI(OpenUI packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.OPEN_UI.getValue());
      mplew.write(packet.ui());
      return mplew.getPacket();
   }

   protected byte[] lockUI(LockUI packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.LOCK_UI.getValue());
      mplew.write(packet.enable() ? 1 : 0);
      return mplew.getPacket();
   }

   protected byte[] disableUI(DisableUI packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DISABLE_UI.getValue());
      mplew.write(packet.enable() ? 1 : 0);
      return mplew.getPacket();
   }

   protected byte[] getKeymap(GetKeyMap packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.KEYMAP.getValue());
      mplew.write(0);
      for (int x = 0; x < 90; x++) {
         KeyBinding binding = packet.bindings().get(x);
         if (binding != null) {
            mplew.write(binding.theType());
            mplew.writeInt(binding.action());
         } else {
            mplew.write(0);
            mplew.writeInt(0);
         }
      }
      return mplew.getPacket();
   }

   protected byte[] getMacros(GetMacros packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MACRO_SYS_DATA_INIT.getValue());
      int count = 0;
      for (int i = 0; i < 5; i++) {
         if (packet.macros()[i] != null) {
            count++;
         }
      }
      mplew.write(count);
      for (int i = 0; i < 5; i++) {
         SkillMacro macro = packet.macros()[i];
         if (macro != null) {
            mplew.writeMapleAsciiString(macro.name());
            mplew.write(macro.shout());
            mplew.writeInt(macro.skill1());
            mplew.writeInt(macro.skill2());
            mplew.writeInt(macro.skill3());
         }
      }
      return mplew.getPacket();
   }

   protected byte[] disableMinimap(DisableMiniMap packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ADMIN_RESULT.getValue());
      mplew.writeShort(0x1C);
      return mplew.getPacket();
   }

   protected byte[] finishedSort(FinishedSort packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.GATHER_ITEM_RESULT.getValue());
      mplew.write(0);
      mplew.write(packet.inventory());
      return mplew.getPacket();
   }

   protected byte[] finishedSort2(FinishedSort2 packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.SORT_ITEM_RESULT.getValue());
      mplew.write(0);
      mplew.write(packet.inventory());
      return mplew.getPacket();
   }

   protected byte[] getClock(GetClock packet) { // time in seconds
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CLOCK.getValue());
      mplew.write(2); // clock type. if you send 3 here you have to send another byte (which does not matter at all) before the timestamp
      mplew.writeInt(packet.time());
      return mplew.getPacket();
   }

   protected byte[] getClockTime(GetClockTime packet) { // Current Time
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CLOCK.getValue());
      mplew.write(1); //Clock-Type
      mplew.write(packet.hour());
      mplew.write(packet.minute());
      mplew.write(packet.second());
      return mplew.getPacket();
   }

   protected byte[] removeClock(StopClock packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STOP_CLOCK.getValue());
      mplew.write(0);
      return mplew.getPacket();
   }
}
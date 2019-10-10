package tools.packet.factory;

import java.util.List;

import client.KeyBinding;
import net.opcodes.SendOpcode;
import net.server.SkillMacro;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.ui.DisableMiniMap;
import tools.packet.ui.DisableUI;
import tools.packet.ui.FinishedSort;
import tools.packet.ui.FinishedSort2;
import tools.packet.ui.GMEffect;
import tools.packet.ui.GetClock;
import tools.packet.ui.GetClockTime;
import tools.packet.ui.GetKeyMap;
import tools.packet.ui.GetMacros;
import tools.packet.ui.LockUI;
import tools.packet.ui.OpenUI;
import tools.packet.ui.RefreshTeleportRockMapList;
import tools.packet.ui.ShowBlockedUI;
import tools.packet.ui.ShowNotes;
import tools.packet.ui.ShowOXQuiz;
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
      registry.setHandler(OpenUI.class, packet -> this.openUI((OpenUI) packet));
      registry.setHandler(LockUI.class, packet -> this.lockUI((LockUI) packet));
      registry.setHandler(DisableUI.class, packet -> this.disableUI((DisableUI) packet));
      registry.setHandler(GetKeyMap.class, packet -> this.getKeymap((GetKeyMap) packet));
      registry.setHandler(GetMacros.class, packet -> this.getMacros((GetMacros) packet));
      registry.setHandler(DisableMiniMap.class, packet -> this.disableMinimap((DisableMiniMap) packet));
      registry.setHandler(FinishedSort.class, packet -> this.finishedSort((FinishedSort) packet));
      registry.setHandler(FinishedSort2.class, packet -> this.finishedSort2((FinishedSort2) packet));
      registry.setHandler(GetClock.class, packet -> this.getClock((GetClock) packet));
      registry.setHandler(GetClockTime.class, packet -> this.getClockTime((GetClockTime) packet));
      registry.setHandler(StopClock.class, packet -> this.removeClock((StopClock) packet));
      registry.setHandler(GMEffect.class, packet -> this.getGMEffect((GMEffect) packet));
      registry.setHandler(ShowBlockedUI.class, packet -> this.blockedMessage2((ShowBlockedUI) packet));
      registry.setHandler(ShowNotes.class, packet -> this.showNotes((ShowNotes) packet));
      registry.setHandler(ShowOXQuiz.class, packet -> this.showOXQuiz((ShowOXQuiz) packet));
      registry.setHandler(RefreshTeleportRockMapList.class, packet -> this.trockRefreshMapList((RefreshTeleportRockMapList) packet));
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

   /**
    * Gets a gm effect packet (ie. hide, banned, etc.)
    * <p>
    * Possible values for <code>type</code>:<br> 0x04: You have successfully
    * blocked access.<br>
    * 0x05: The unblocking has been successful.<br> 0x06 with Mode 0: You have
    * successfully removed the name from the ranks.<br> 0x06 with Mode 1: You
    * have entered an invalid character name.<br> 0x10: GM Hide, mode
    * determines whether or not it is on.<br> 0x1E: Mode 0: Failed to send
    * warning Mode 1: Sent warning<br> 0x13 with Mode 0: + mapid 0x13 with Mode
    * 1: + ch (FF = Unable to find merchant)
    */
   protected byte[] getGMEffect(GMEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ADMIN_RESULT.getValue());
      mplew.write(packet.theType());
      mplew.write(packet.mode());
      return mplew.getPacket();
   }

   /**
    * Gets a "block" packet (ie. the cash shop is unavailable, etc)
    * <p>
    * Possible values for <code>type</code>:<br> 1: You cannot move that
    * channel. Please try again later.<br> 2: You cannot go into the cash shop.
    * Please try again later.<br> 3: The Item-Trading Shop is currently
    * unavailable. Please try again later.<br> 4: You cannot go into the trade
    * shop, due to limitation of user count.<br> 5: You do not meet the minimum
    * level requirement to access the Trade Shop.<br>
    */
   protected byte[] blockedMessage2(ShowBlockedUI packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BLOCKED_SERVER.getValue());
      mplew.write(packet.theType());
      return mplew.getPacket();
   }

   protected byte[] showNotes(ShowNotes packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MEMO_RESULT.getValue());
      mplew.write(3);
      mplew.write(packet.notes().size());
      packet.notes().forEach(note -> {
         mplew.writeInt(note.id());
         mplew.writeMapleAsciiString(note.from() + " "); //Stupid nexon forgot space lol
         mplew.writeMapleAsciiString(note.message());
         mplew.writeLong(getTime(note.timestamp()));
         mplew.write(note.fame()); //FAME :D
      });
      return mplew.getPacket();
   }

   protected byte[] showOXQuiz(ShowOXQuiz packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.OX_QUIZ.getValue());
      mplew.write(packet.askQuestion() ? 1 : 0);
      mplew.write(packet.questionSet());
      mplew.writeShort(packet.questionId());
      return mplew.getPacket();
   }

   protected byte[] trockRefreshMapList(RefreshTeleportRockMapList packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAP_TRANSFER_RESULT.getValue());
      mplew.write(packet.delete() ? 2 : 3);
      if (packet.vip()) {
         mplew.write(1);
         List<Integer> map = packet.vips();
         for (int i = 0; i < 10; i++) {
            mplew.writeInt(map.get(i));
         }
      } else {
         mplew.write(0);
         List<Integer> map = packet.regulars();
         for (int i = 0; i < 5; i++) {
            mplew.writeInt(map.get(i));
         }
      }
      return mplew.getPacket();
   }
}
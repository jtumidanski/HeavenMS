package tools.packet.factory;

import java.util.List;

import client.KeyBinding;
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
      Handler.handle(OpenUI.class).decorate(this::openUI).size(3).register(registry);
      Handler.handle(LockUI.class).decorate(this::lockUI).size(3).register(registry);
      Handler.handle(DisableUI.class).decorate(this::disableUI).register(registry);
      Handler.handle(GetKeyMap.class).decorate(this::getKeymap).register(registry);
      Handler.handle(GetMacros.class).decorate(this::getMacros).register(registry);
      Handler.handle(DisableMiniMap.class).decorate(this::disableMiniMap).register(registry);
      Handler.handle(FinishedSort.class).decorate(this::finishedSort).size(4).register(registry);
      Handler.handle(FinishedSort2.class).decorate(this::finishedSort2).size(4).register(registry);
      Handler.handle(GetClock.class).decorate(this::getClock).register(registry);
      Handler.handle(GetClockTime.class).decorate(this::getClockTime).register(registry);
      Handler.handle(StopClock.class).decorate(this::removeClock).register(registry);
      Handler.handle(GMEffect.class).decorate(this::getGMEffect).register(registry);
      Handler.handle(ShowBlockedUI.class).decorate(this::blockedMessage2).register(registry);
      Handler.handle(ShowNotes.class).decorate(this::showNotes).register(registry);
      Handler.handle(ShowOXQuiz.class).decorate(this::showOXQuiz).size(6).register(registry);
      Handler.handle(RefreshTeleportRockMapList.class).decorate(this::refreshTeleportRockMapList).register(registry);
   }

   /**
    * Sends a UI utility. 0x01 - Equipment Inventory. 0x02 - Stat Window. 0x03
    * - Skill Window. 0x05 - Keyboard Settings. 0x06 - Quest window. 0x09 -
    * Monster book Window. 0x0A - Char Info 0x0B - Guild BBS 0x12 - Monster
    * Carnival Window 0x16 - Party Search. 0x17 - Item Creation Window. 0x1A -
    * My Ranking O.O 0x1B - Family Window 0x1C - Family Pedigree 0x1D - GM
    * Story Board /funny shit 0x1E - Envelop saying you got mail from an admin.
    * 0x1F - Medal Window 0x20 - Maple Event (???) 0x21 - Invalid Pointer
    * Crash
    */
   protected void openUI(MaplePacketLittleEndianWriter writer, OpenUI packet) {
      writer.write(packet.ui());
   }

   protected void lockUI(MaplePacketLittleEndianWriter writer, LockUI packet) {
      writer.write(packet.enable() ? 1 : 0);
   }

   protected void disableUI(MaplePacketLittleEndianWriter writer, DisableUI packet) {
      writer.write(packet.enable() ? 1 : 0);
   }

   protected void getKeymap(MaplePacketLittleEndianWriter writer, GetKeyMap packet) {
      writer.write(0);
      for (int x = 0; x < 90; x++) {
         KeyBinding binding = packet.bindings().get(x);
         if (binding != null) {
            writer.write(binding.theType());
            writer.writeInt(binding.action());
         } else {
            writer.write(0);
            writer.writeInt(0);
         }
      }
   }

   protected void getMacros(MaplePacketLittleEndianWriter writer, GetMacros packet) {
      int count = 0;
      for (int i = 0; i < 5; i++) {
         if (packet.macros()[i] != null) {
            count++;
         }
      }
      writer.write(count);
      for (int i = 0; i < 5; i++) {
         SkillMacro macro = packet.macros()[i];
         if (macro != null) {
            writer.writeMapleAsciiString(macro.name());
            writer.write(macro.shout());
            writer.writeInt(macro.skill1());
            writer.writeInt(macro.skill2());
            writer.writeInt(macro.skill3());
         }
      }
   }

   protected void disableMiniMap(MaplePacketLittleEndianWriter writer, DisableMiniMap packet) {
      writer.writeShort(0x1C);
   }

   protected void finishedSort(MaplePacketLittleEndianWriter writer, FinishedSort packet) {
      writer.write(0);
      writer.write(packet.inventory());
   }

   protected void finishedSort2(MaplePacketLittleEndianWriter writer, FinishedSort2 packet) {
      writer.write(0);
      writer.write(packet.inventory());
   }

   protected void getClock(MaplePacketLittleEndianWriter writer, GetClock packet) { // time in seconds
      writer.write(2); // clock type. if you send 3 here you have to send another byte (which does not matter at all) before the timestamp
      writer.writeInt(packet.time());
   }

   protected void getClockTime(MaplePacketLittleEndianWriter writer, GetClockTime packet) { // Current Time
      writer.write(1); //Clock-Type
      writer.write(packet.hour());
      writer.write(packet.minute());
      writer.write(packet.second());
   }

   protected void removeClock(MaplePacketLittleEndianWriter writer, StopClock packet) {
      writer.write(0);
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
    * warning Mode 1: Sent warning<br> 0x13 with Mode 0: + map id 0x13 with Mode
    * 1: + ch (FF = Unable to find merchant)
    */
   protected void getGMEffect(MaplePacketLittleEndianWriter writer, GMEffect packet) {
      writer.write(packet.theType());
      writer.write(packet.mode());
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
   protected void blockedMessage2(MaplePacketLittleEndianWriter writer, ShowBlockedUI packet) {
      writer.write(packet.theType());
   }

   protected void showNotes(MaplePacketLittleEndianWriter writer, ShowNotes packet) {
      writer.write(3);
      writer.write(packet.notes().size());
      packet.notes().forEach(note -> {
         writer.writeInt(note.id());
         writer.writeMapleAsciiString(note.from() + " ");
         writer.writeMapleAsciiString(note.message());
         writer.writeLong(getTime(note.timestamp()));
         writer.write(note.fame());
      });
   }

   protected void showOXQuiz(MaplePacketLittleEndianWriter writer, ShowOXQuiz packet) {
      writer.write(packet.askQuestion() ? 1 : 0);
      writer.write(packet.questionSet());
      writer.writeShort(packet.questionId());
   }

   protected void refreshTeleportRockMapList(MaplePacketLittleEndianWriter writer, RefreshTeleportRockMapList packet) {
      writer.write(packet.delete() ? 2 : 3);
      if (packet.vip()) {
         writer.write(1);
         List<Integer> map = packet.vips();
         for (int i = 0; i < 10; i++) {
            writer.writeInt(map.get(i));
         }
      } else {
         writer.write(0);
         List<Integer> map = packet.regulars();
         for (int i = 0; i < 5; i++) {
            writer.writeInt(map.get(i));
         }
      }
   }
}
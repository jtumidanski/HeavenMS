package quest

import client.MapleJob
import client.inventory.MapleInventoryType
import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest3414 {
   QuestActionManager qm
   int status = -1

   int item
   MapleJob stance
   int[] vecItem

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0) {
         qm.dispose()
         return
      }
      status++

      if (status == 0) {
         qm.sendNext(I18nMessage.from("3414_WHOA"))
      } else if (status == 1) {
         if (qm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_FULL_ERROR"))
            qm.dispose()
            return
         }

         String talkStr = "Here, please select the scroll of your choice. All success rates are at 10%. \r\n\r\n#rSELECT A ITEM\r\n#b"
         stance = qm.getPlayer().getJobStyle()

         if (stance == MapleJob.WARRIOR || stance == MapleJob.BEGINNER) {
            vecItem = [2043002, 2043102, 2043202, 2044002, 2044102, 2044202, 2044402, 2044302]
         } else if (stance == MapleJob.MAGICIAN) {
            vecItem = [2043702, 2043802]
         } else if (stance == MapleJob.BOWMAN || stance == MapleJob.CROSSBOWMAN) {
            vecItem = [2044502, 2044602]
         } else if (stance == MapleJob.THIEF) {
            vecItem = [2043302, 2044702]
         } else {
            vecItem = [2044802, 2044902]
         }

         for (int i = 0; i < vecItem.length; i++) {
            talkStr += "\r\n#L" + i + "# #i" + vecItem[i] + "# #t" + vecItem[i] + "#"
         }
         qm.sendSimple(talkStr)
      } else if (status == 2) {
         item = vecItem[selection]
         qm.gainItem(item, (short) 1)
         qm.gainItem(4031103, (short) -1)
         qm.gainItem(4031104, (short) -1)
         qm.gainItem(4031105, (short) -1)
         qm.gainItem(4031106, (short) -1)
         qm.gainExp(12000)
         qm.completeQuest()

         qm.dispose()
      }
   }
}

Quest3414 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3414(qm: qm))
   }
   return (Quest3414) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}
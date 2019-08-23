package quest

import client.MapleJob
import client.inventory.MapleInventoryType
import scripting.quest.QuestActionManager

class Quest2001 {
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
         qm.sendNext("THIS is the deed to the land that my son lost! And you even brought all the necessary materials to build the house! Thank you so much ... my relatives can all move in and live in #m102000000#! As a sign of appreciation ...")
      } else if (status == 1) {
         if (qm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            qm.getPlayer().dropMessage(1, "USE inventory full.")
            qm.dispose()
            return
         }

         String talkStr = "Okay, now choose the scroll of your liking ... The odds of winning are 10% each. \r\n\r\n#rSELECT A ITEM\r\n#b"
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
         qm.gainItem(4000022, (short) -100)
         qm.gainItem(4003000, (short) -30)
         qm.gainItem(4003001, (short) -30)
         qm.gainItem(4001004, (short) -1)
         qm.gainExp(20000)
         qm.gainMeso(15000)
         qm.gainFame(2)
         qm.completeQuest()

         qm.dispose()
      }
   }
}

Quest2001 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2001(qm: qm))
   }
   return (Quest2001) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}
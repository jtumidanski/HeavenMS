package quest

import client.inventory.MapleInventoryType
import scripting.quest.QuestActionManager

class Quest3833 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         qm.dispose()
      } else {
         if (status == 0) {
            qm.sendOk("Great! You managed to get the herb I need. As a #btoken of gratitude#k, take this item to help on your journey.")
         } else if (status == 1) {
            if (qm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 2) {
               if (qm.haveItem(4000294, 1000)) {
                  qm.gainItem(4000294, (short) -1000)
                  qm.gainItem(2040501, (short) 1)
                  qm.gainItem(2000005, (short) 50)
                  qm.gainExp(54000)
                  qm.forceCompleteQuest()
               } else if (qm.haveItem(4000294, 600)) {
                  qm.gainItem(4000294, (short) -600)
                  qm.gainItem(2020013, (short) 50)
                  qm.gainExp(54000)
                  qm.forceCompleteQuest()
               } else if (qm.haveItem(4000294, 500)) {
                  qm.gainItem(4000294, (short) -500)
                  qm.gainExp(54000)
                  qm.forceCompleteQuest()
               } else if (qm.haveItem(4000294, 100)) {
                  qm.gainItem(4000294, (short) -100)
                  qm.gainExp(45000)
                  qm.forceCompleteQuest()
               } else if (qm.haveItem(4000294, 50)) {
                  qm.gainItem(4000294, (short) -50)
                  qm.gainItem(2020007, (short) 50)
                  qm.gainExp(10000)
                  qm.forceCompleteQuest()
               } else if (qm.haveItem(4000294, 1)) {
                  qm.gainItem(4000294, (short) -1)
                  qm.gainItem(2000000, (short) 1)
                  qm.gainExp(10)
                  qm.forceCompleteQuest()
               }
               qm.dispose()
            } else {
               qm.sendOk("Could you make #b2 slots available#k on your USE inventory before receiving your reward?")
            }
         } else if (status == 2) {
            qm.dispose()
         }
      }
   }
}

Quest3833 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3833(qm: qm))
   }
   return (Quest3833) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}
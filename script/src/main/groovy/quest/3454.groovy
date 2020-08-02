package quest
import tools.I18nMessage

import client.inventory.MapleInventoryType
import scripting.quest.QuestActionManager

class Quest3454 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (qm.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1) {
               qm.sendOk(I18nMessage.from("3454_ETC_ROOM_NEEDED"))
               qm.dispose()
               return
            }

            qm.gainItem(4000125, (short) -1)
            qm.gainItem(4031926, (short) -10)
            qm.gainItem(4000119, (short) -30)
            qm.gainItem(4000118, (short) -30)

            double rnd = Math.random()
            if (rnd < 1.0) {
               qm.gainItem(4031928, (short) 1)
            } else {
               qm.gainItem(4031927, (short) 1)
            }

            qm.sendOk(I18nMessage.from("3454_GO_MEET"))
            qm.forceCompleteQuest()
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest3454 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3454(qm: qm))
   }
   return (Quest3454) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}
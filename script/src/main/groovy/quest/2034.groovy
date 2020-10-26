package quest

import constants.MapleJob
import constants.MapleInventoryType
import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest2034 {
   QuestActionManager qm
   int status = -1

   int item

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0) {
         qm.dispose()
         return
      }
      status++

      if (status == 0) {
         qm.sendNext(I18nMessage.from("2034_I_KNEW_IT"))
      } else if (status == 1) {
         if (qm.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
            qm.sendOk(I18nMessage.from("2034_FREE_EQUIP_SLOT"))
            qm.dispose()
            return
         }

         MapleJob stance = qm.getPlayer().getJobStyle()

         if (stance == MapleJob.WARRIOR) {
            item = 1072003
         } else if (stance == MapleJob.MAGICIAN) {
            item = 1072077
         } else if (stance == MapleJob.BOWMAN || stance == MapleJob.CROSSBOWMAN) {
            item = 1072081
         } else if (stance == MapleJob.THIEF) {
            item = 1072035
         } else if (stance == MapleJob.BRAWLER || stance == MapleJob.GUNSLINGER) {
            item = 1072294
         } else {
            item = 1072018
         }

         qm.gainItem(item, (short) 1)
         qm.gainItem(4000007, (short) -150)
         qm.gainExp(2200)
         qm.completeQuest()

         qm.sendOk(I18nMessage.from("2034_ALRIGHT"))
         qm.dispose()
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2034 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2034(qm: qm))
   }
   return (Quest2034) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}
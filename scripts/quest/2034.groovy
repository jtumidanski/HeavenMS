package quest

import client.MapleJob
import client.inventory.MapleInventoryType
import scripting.quest.QuestActionManager

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
         qm.sendNext("I knew it ... I knew you could get it done with, quickly! You did your job well last time, and here you are again, taking care of business!! Alright, since you have done it so well, I should reward you well. #b#p1051000##k is giving you a pair of shoes in hopes of helping you out on your future traveling.")
      } else if (status == 1) {
         if (qm.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1) {
            qm.sendOk("Please free a EQUIP inventory slot to receive the reward.")
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

         qm.sendOk("Alright, if you need work sometime down the road, feel free to come back and see me. This town sure can use a person like you for help~")
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